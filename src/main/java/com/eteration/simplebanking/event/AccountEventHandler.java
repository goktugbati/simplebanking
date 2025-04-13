package com.eteration.simplebanking.event;

import com.eteration.simplebanking.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventHandler {

    private final KafkaProducerService producer;

    @EventListener
    public void handleAccountEvent(AccountEvent event) {
        log.info("📤 Handling AccountEvent: {}", event);
        producer.publish(event);
    }
}
