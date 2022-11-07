package com.szekai.orderinquiryservice.service;

import com.szekai.orderinquiryservice.vo.OrderNotFoundException;
import com.szekai.orderinquiryservice.vo.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static com.szekai.orderinquiryservice.OrderInquiryServiceApplication.STATE_STORE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final InteractiveQueryService interactiveQueryService;

    public Function<UUID, OrderStatus> statusCheck() {
        return orderUuid -> {
            final ReadOnlyKeyValueStore<UUID, String> store =
                    interactiveQueryService.getQueryableStore(STATE_STORE_NAME, QueryableStoreTypes.keyValueStore());
            HostInfo hostInfo = interactiveQueryService.getHostInfo(STATE_STORE_NAME,
                    orderUuid, new UUIDSerializer());

            log.debug("key located in: {}", hostInfo);
            if (interactiveQueryService.getCurrentHostInfo().equals(hostInfo)) {
                //get it from current app store
                return OrderStatus.valueOf(Optional.ofNullable(store.get(orderUuid))
                        .orElseThrow(() -> new OrderNotFoundException("Order not found")));
            } else {
                //get it from remote app store
                return new RestTemplate().getForEntity(
                        String.format("%s://%s:%d/order/status/%s", "http", hostInfo.host(), hostInfo.port(), orderUuid)
                        , OrderStatus.class).getBody();
            }
        };
    }
}
