package com.szekai.orderprocessor.service;

import com.szekai.orderprocessor.vo.Order;
import com.szekai.orderprocessor.vo.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.szekai.orderprocessor.service.OrderTopology.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final Serde<Order> orderJsonSerde;

    @Bean
    public Function<KStream<UUID, Order>, KStream<UUID, Order>> orderStateStoreProcessor() {
        return uuidOrderKStream -> {
            KTable<UUID, String> uuidStringKTable = kStreamKTableStringFunction.apply(uuidOrderKStream);

            //then join the stream with its original stream to keep the flow
            return uuidOrderKStream.leftJoin(uuidStringKTable,
                    (order, status) -> order,
                    Joined.with(Serdes.UUID(), orderJsonSerde, Serdes.String()));
        };
    }

    @Bean
    @SuppressWarnings("unchecked")
    public Function<KStream<UUID, Order>, KStream<UUID, Order>[]> orderProcess() {

        return input -> input
                .peek((uuid, order) -> log.debug("Routing Order: {} [status: {}]", uuid, order.getOrderStatus()))
                .map((uuid, order) -> {
                    try {
                        //just a dummy delay
                        Thread.sleep(5_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return new KeyValue<>(uuid, order);
                })
                .map(KeyValue::new)
                .branch(isOrderMadePredicate, isInventoryCheckedPredicate, isShippedPredicate);
    }

    @Bean
    public Function<KStream<UUID, Order>, KStream<UUID, Order>> inventoryCheck() {
        return input -> input
                .peek((uuid, order) -> log.debug("Checking order inventory, Order: {}", uuid))
                .peek((key, value) -> value.setOrderStatus(OrderStatus.INVENTORY_CHECKING))
                .map(KeyValue::new);
    }

    @Bean
    public Function<KStream<UUID, Order>, KStream<UUID, Order>> shipping() {
        return input -> input
                .peek((uuid, order) -> log.debug("Applying Shipping Process, Order: {}", uuid))
                .peek((key, value) -> value.setOrderStatus(OrderStatus.SHIPPED))
                .map(KeyValue::new);
    }

    @Bean
    public Consumer<KStream<UUID, Order>> shippedConsumer() {
        return input -> input
                .foreach((key, value) -> log.debug("THIS IS THE END! key: {} value: {}", key, value));
    }
}
