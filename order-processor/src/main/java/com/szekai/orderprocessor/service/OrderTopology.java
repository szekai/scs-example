package com.szekai.orderprocessor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szekai.orderprocessor.vo.Order;
import com.szekai.orderprocessor.vo.OrderStatus;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.UUID;
import java.util.function.Function;

import static com.szekai.orderprocessor.OrderProcessorApplication.STATE_STORE_NAME;

public interface OrderTopology {
    Predicate<UUID, Order> isOrderMadePredicate = (k, v) -> v.getOrderStatus().equals(OrderStatus.PENDING);
    Predicate<UUID, Order> isInventoryCheckedPredicate = (k, v) -> v.getOrderStatus().equals(OrderStatus.INVENTORY_CHECKING);
    Predicate<UUID, Order> isShippedPredicate = (k, v) -> v.getOrderStatus().equals(OrderStatus.SHIPPED);

    Function<KStream<UUID, Order>, KTable<UUID, String>> kStreamKTableStringFunction = input -> input
            .groupBy((s, order) -> order.getOrderUuid(),
                    Grouped.with(null, new JsonSerde<>(Order.class, new ObjectMapper())))
            .aggregate(
                    String::new,
                    (s, order, oldStatus) -> order.getOrderStatus().toString(),
                    Materialized.<UUID, String, KeyValueStore<Bytes, byte[]>>as(STATE_STORE_NAME)
                            .withKeySerde(Serdes.UUID()).
                            withValueSerde(Serdes.String())
            );
}
