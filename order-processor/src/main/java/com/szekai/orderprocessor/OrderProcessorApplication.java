package com.szekai.orderprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderProcessorApplication {

	public final static String STATE_STORE_NAME = "order-events";

	public static void main(String[] args) {
		SpringApplication.run(OrderProcessorApplication.class, args);
	}

}
