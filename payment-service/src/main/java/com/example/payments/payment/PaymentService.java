package com.example.payments.payment;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;



@Service
public class PaymentService {

    public record CreateResult(PaymentResponse  payment, boolean created){

    }
    private final PaymentRepository repository;
    public PaymentService(PaymentRepository repository){
            this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PaymentResponse get(UUID id){
        return repository.findById(id)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    private CreateResult replay(Payment payment, String requestHash, String idempotencyKey){
        if(!payment.getRequestHash().equals(requestHash)){
            throw new IdempotencyKeyConflictException(idempotencyKey);
        }
        return new CreateResult(PaymentResponse.from(payment), false);
    }


    public CreateResult create(String idempotencyKey, CreatePaymentRequest request) {
        String requestHash = hashOf(request);

        Optional<Payment> existing = repository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return replay(existing.get(), requestHash, idempotencyKey);
        }

        try {
            Payment saved = repository.saveAndFlush(Payment.newPending(
                    idempotencyKey,
                    requestHash,
                    request.payerAccountId(),
                    request.payeeAccountId(),
                    request.amountMinor(),
                    request.currency()

            ));
            return new CreateResult(PaymentResponse.from(saved), true);
        } catch (DataIntegrityViolationException e) {

            Payment winner = repository.findByIdempotencyKey(idempotencyKey).orElseThrow(() -> e);
            return replay(winner, requestHash, idempotencyKey);
        }
    }
    private static  String hashOf(CreatePaymentRequest r){

        String canonical = String.join("|",
                        r.payerAccountId(),
                        r.payeeAccountId(),
                        String.valueOf(r.amountMinor()),
                        r.currency());
              try {

                  MessageDigest digest = MessageDigest.getInstance("SHA-256");
                  return HexFormat.of().formatHex(digest.digest(canonical.getBytes(StandardCharsets.UTF_8)));
              } catch (NoSuchAlgorithmException e){
                  throw new IllegalStateException("SHA-256 not available", e);
              }

    }



}

