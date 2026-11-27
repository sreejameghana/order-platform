# Order Platform: Payments Service

A local payments backend I built to practice the patterns fintech systems rely on: idempotent APIs, correct money handling, and event-driven design. It is a learning project and does not process real money.

## Tech stack

Java 21 · Spring Boot 4 · Spring Data JPA (Hibernate 7) · PostgreSQL 16 · Flyway · Apache Kafka 3.9 · Docker Compose

## What it does today

| Endpoint | Description |
|---|---|
| `POST /payments` | Creates a payment. Requires an `Idempotency-Key` header |
| `GET /payments/{id}` | Returns one payment |

## Design decisions

- **Idempotency:** a `UNIQUE` constraint on the idempotency key guarantees one payment per key, even under concurrent requests. A retry returns the original payment with `200`; a new payment returns `201` with a `Location` header.
- **Key reuse detection:** a SHA-256 hash of the request body is stored with each payment. Reusing a key with a different body returns `422`.
- **Money as integer minor units:** amounts are stored as `BIGINT` cents with an ISO currency code, never as floating point.
- **Schema owned by Flyway:** Hibernate runs with `ddl-auto=validate`, so every schema change is a versioned migration.
- **Consistent errors:** all errors use RFC 9457 `ProblemDetail` JSON.
- **Virtual threads:** enabled for request handling (Java 21).

## Run locally

Requirements: Docker Desktop and Java 21.

```bash
docker compose up -d
cd payment-service
./mvnw spring-boot:run
```

Create a payment:

```bash
curl -i -X POST localhost:8080/payments \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: test-001" \
  -d '{"payerAccountId":"acct-111","payeeAccountId":"acct-222","amountMinor":1000,"currency":"USD"}'
```

Send the same request again: it returns `200` with the same payment id.

Kafka UI is available at http://localhost:8085.

## Roadmap

- [ ] Tests (JUnit 5, Mockito, Spring Boot Test)
- [ ] Payment events via Kafka with the transactional outbox pattern
- [ ] Fraud rules engine (SpEL)
- [ ] OAuth2 / JWT security with Keycloak
- [ ] AI support assistant with Spring AI (MCP tools, RAG on pgvector)