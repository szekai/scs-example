package com.szekai.orderservice.service;

import com.szekai.orderservice.vo.Order;
import com.szekai.orderservice.vo.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements OrderTopology  {
    private final Serde<Order> orderJsonSerde;

    @Value("${spring.cloud.stream.bindings.orderStateStoreProcessor-in-0.destination}")
    private String orderTopic;

    @Value("${spring.cloud.stream.kafka.streams.binder.brokers}")
    private String bootstrapServer;

    public Function<Order, Order> placeOrder() {
        return orderIn -> {
            //create an order
            var order = Order.builder()//
                    .itemName(orderIn.getItemName())//
                    .orderUuid(UUID.randomUUID())//
                    .orderStatus(OrderStatus.PENDING)//
                    .build();

            //producer
            new KafkaTemplate<>(orderJsonSerdeFactoryFunction
                    .apply(orderJsonSerde.serializer(), bootstrapServer), true) {{
                setDefaultTopic(orderTopic);
                sendDefault(order.getOrderUuid(), order);
            }};
            return order;
        };
    }
}
