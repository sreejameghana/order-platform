package com.example.payments.payment;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, updatable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "request_hash", nullable = false, updatable = false, length = 64)
    private String requestHash;

    @Column(name="payer_account_id", nullable = false, updatable = false, length = 50)
    private String payerAccountId;

    @Column(name="payee_account_id", nullable = false, updatable = false, length = 50)
    private String payeeAccountId;

    @Column(name="amount_minor", nullable = false, updatable = false)
    private Long amountMinor;


    @Column(name="currency", nullable = false, updatable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name="created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


    protected Payment(){

    }

public static Payment newPending(String idempotencyKey, String requestHash,String payerAccountId, String payeeAccountId,
                                  Long amountMinor, String currency ) {

        Payment p = new Payment();
        p.idempotencyKey = idempotencyKey;
        p.requestHash = requestHash;
        p.payerAccountId = payerAccountId;
        p.payeeAccountId = payeeAccountId;
        p.amountMinor = amountMinor;
        p.currency = currency;
        p.status = PaymentStatus.PENDING;
        Instant now = Instant.now();
        p.createdAt = now;
        p.updatedAt = now;
        return p;
}

  @PreUpdate
  void onUpdate(){
        this.updatedAt = Instant.now();

  }

  public UUID getId(){ return id;}
  public String getIdempotencyKey(){return idempotencyKey;}
  public String getRequestHash(){return requestHash;}
  public String getPayerAccountId(){return payerAccountId;}
  public String getPayeeAccountId(){return payeeAccountId;}
  public Long getAmountMinor(){return amountMinor;}
  public String getCurrency(){return currency;}
  public PaymentStatus getStatus(){return status;}
  public Instant getCreatedAt(){return createdAt;}
  public Instant getUpdatedAt(){return updatedAt;}



}
