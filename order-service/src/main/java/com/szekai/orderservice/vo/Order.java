package com.szekai.orderservice.vo;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.UUID;

@ToString
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    private UUID orderUuid;

    @NotBlank
    private String itemName;

    private OrderStatus orderStatus;
}
