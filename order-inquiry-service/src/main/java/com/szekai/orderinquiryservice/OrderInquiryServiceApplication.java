package com.szekai.orderinquiryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderInquiryServiceApplication {

	public final static String STATE_STORE_NAME = "order-events";

	public static void main(String[] args) {
		SpringApplication.run(OrderInquiryServiceApplication.class, args);
	}

}
