package com.example.payments.payment;

import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        String payerAccountId,
        String payeeAccountId,
        long amountMinor,
        String currency,
        PaymentStatus status,
        Instant createdAt
) {
 static PaymentResponse from(Payment p){
     return new PaymentResponse(
             p.getId(),
             p.getPayerAccountId(),
             p.getPayeeAccountId(),
             p.getAmountMinor(),
             p.getCurrency(),
             p.getStatus(),
             p.getCreatedAt());

 }

}
