package com.szekai.orderinquiryservice.controller;

import com.szekai.orderinquiryservice.service.OrderService;
import com.szekai.orderinquiryservice.vo.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("order/status/{orderUuid}")
    public Mono<OrderStatus> statusCheck(@PathVariable("orderUuid") UUID orderUuid) {
        return Mono.just(orderUuid).map(orderService.statusCheck());
//        return orderService.statusCheck().apply(orderUuid);
    }
}
