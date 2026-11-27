package com.example.payments.payment;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final  PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(
      @RequestHeader("Idempotency-Key") @NotBlank @Size(max=100) String idempotencyKey,
      @Valid @RequestBody CreatePaymentRequest request){


      PaymentService.CreateResult  result =  paymentService.create(idempotencyKey, request);

      if(result.created()){
          URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                  .path("/{id}")
                  .buildAndExpand(result.payment().id())
                  .toUri();
          return ResponseEntity.created(location).body(result.payment());
      }
      return ResponseEntity.ok(result.payment());
    }

    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable UUID id){
        return paymentService.get(id);
    }

}
