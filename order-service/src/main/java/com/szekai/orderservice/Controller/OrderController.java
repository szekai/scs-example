package com.szekai.orderservice.Controller;

import com.szekai.orderservice.service.OrderService;
import com.szekai.orderservice.vo.Order;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("order")
    public Mono<Order> placeOrder(@RequestBody @NotNull(message = "Invalid Order") Order order) {
        return Mono.just(order).map(orderService.placeOrder());
//        return orderService.placeOrder().apply(order);
    }
}
