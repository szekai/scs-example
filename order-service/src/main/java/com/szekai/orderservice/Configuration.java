package com.szekai.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szekai.orderservice.vo.Order;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    public Serde<Order> orderJsonSerde() {
        return new JsonSerde<>(Order.class, new ObjectMapper());
    }
}
