package com.eteration.simplebanking.outbox;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "outbox_event")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    private String accountNumber;

    private double amount;

    private String approvalCode;

    private ZonedDateTime timestamp;

    private boolean published;
}
