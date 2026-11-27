package com.example.payments.payment;

import jakarta.validation.constraints.*;

public record CreatePaymentRequest(
    @NotBlank @Size(max = 50) String payerAccountId,
    @NotBlank @Size(max = 50) String payeeAccountId,
    @NotNull @Positive Long amountMinor,
    @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency
)
{}
