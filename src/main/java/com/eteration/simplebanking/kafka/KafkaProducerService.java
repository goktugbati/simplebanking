package com.eteration.simplebanking.kafka;

import com.eteration.simplebanking.event.AccountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, AccountEvent> kafkaTemplate;
    private final String TOPIC = "account-events";

    public void publish(AccountEvent event) {
        kafkaTemplate.send(TOPIC, event.getAccountNumber(), event);
    }
}
