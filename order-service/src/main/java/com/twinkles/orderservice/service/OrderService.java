package com.twinkles.orderservice.service;

import com.twinkles.orderservice.dto.OrderRequest;

public interface OrderService {
    String placeOrder(OrderRequest orderRequest);
}
