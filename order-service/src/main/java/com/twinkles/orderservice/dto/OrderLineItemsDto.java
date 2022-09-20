package com.twinkles.orderservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLineItemsDto {
    private long id;
    private String skuCode;
    private Integer quantity;
    private BigDecimal price;
}
