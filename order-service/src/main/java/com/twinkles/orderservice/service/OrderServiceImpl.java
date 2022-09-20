package com.twinkles.orderservice.service;

import com.twinkles.orderservice.dto.InventoryResponse;
import com.twinkles.orderservice.dto.OrderLineItemsDto;
import com.twinkles.orderservice.dto.OrderRequest;
import com.twinkles.orderservice.event.OrderPlacedEvent;
import com.twinkles.orderservice.model.Order;
import com.twinkles.orderservice.model.OrderLineItems;
import com.twinkles.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    @Override
    public String placeOrder(OrderRequest orderRequest) {
       Order order = new Order();
       order.setOrderNumber(UUID.randomUUID().toString());
       List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsList().stream()
               .map(this::mapToOrderLineItems).toList();

       order.setOrderLineItemsList(orderLineItemsList);

      List<String>skuCodes =  order.getOrderLineItemsList().stream().
              map(OrderLineItems::getSkuCode).toList();
        Span inventoryServiceLookUp = tracer.nextSpan().name("InventoryServiceLookUp");
        try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookUp.start())){

            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class).block();

            boolean allProductsInStock = true;
            for (InventoryResponse inventoryResponse : inventoryResponseArray) {
                if (!inventoryResponse.isInStock()) {
                    allProductsInStock = false;
                    break;
                }
            }

            if(allProductsInStock){
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber(), order.getEmailAddress()));
                return "Order successfully placed";
            }
            throw new IllegalArgumentException("Product is not in stock, please try again later");

        }finally {
            inventoryServiceLookUp.end();
        }

    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .id(orderLineItemsDto.getId())
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .skuCode(orderLineItemsDto.getSkuCode())
                .build();
    }


}
