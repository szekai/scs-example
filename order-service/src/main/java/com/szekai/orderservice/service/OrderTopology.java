package com.szekai.orderservice.service;

import com.szekai.orderservice.vo.Order;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public interface OrderTopology {
    BiFunction<Serializer<Order>, String, DefaultKafkaProducerFactory<UUID, Order>> orderJsonSerdeFactoryFunction
            = (orderSerde, bootstrapServer) -> new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer,
            ProducerConfig.RETRIES_CONFIG, 0,
            ProducerConfig.BATCH_SIZE_CONFIG, 16384,
            ProducerConfig.LINGER_MS_CONFIG, 1,
            ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, orderSerde.getClass()));
}
