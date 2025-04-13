package com.eteration.simplebanking.outbox;

import com.eteration.simplebanking.event.AccountEvent;
import com.eteration.simplebanking.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventRelay {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProducerService kafkaProducerService;

    @Scheduled(fixedRate = 2000)
    @Transactional
    public void publishUnsentEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByPublishedFalse();

        for (OutboxEvent outbox : events) {
            try {
                kafkaProducerService.publish(new AccountEvent(
                        outbox.getEventType(),
                        outbox.getAccountNumber(),
                        outbox.getAmount(),
                        outbox.getApprovalCode(),
                        outbox.getTimestamp()
                ));

                outbox.setPublished(true);
                log.info("✅ Published outbox event to Kafka: {}", outbox.getApprovalCode());

            } catch (Exception e) {
                log.error("❌ Failed to publish outbox event: {}", outbox.getApprovalCode(), e);
            }
        }
    }
}
