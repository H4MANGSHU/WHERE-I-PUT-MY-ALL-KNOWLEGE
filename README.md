
A production-grade, non-blocking SMS messaging microservice built for resilience, observability, and zero-downtime operations.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Infrastructure — Terraform + Docker (Redis)](#infrastructure--terraform--docker-redis)
- [Secrets Management — HashiCorp Vault via Terraform](#secrets-management--hashicorp-vault-via-terraform)
- [Kubernetes — Replication & Zero-Downtime](#kubernetes--replication--zero-downtime)
- [Redis Pub/Sub + WebSocket](#redis-pubsub--websocket)
- [Non-Blocking I/O with Mono](#non-blocking-io-with-mono)
- [ML Model Integration via Mono](#ml-model-integration-via-mono)
- [Health Check & IP-Based Security](#health-check--ip-based-security)
- [OAuth Authentication](#oauth-authentication)
- [Resilience — Rate Limiter](#resilience--rate-limiter)
- [Observability — Grafana + Micrometer](#observability--grafana--micrometer)
- [Error Handling — 4xx / 5xx](#error-handling--4xx--5xx)
- [Resource Limits](#resource-limits)
- [Getting Started](#getting-started)

---

## Architecture Overview

```
  Client / Admin
       │
       │ OAuth Login
       │ Static IP ──► /health only
       ▼
  ┌─────────────────────────────────────────────────┐
  │               Kubernetes Cluster                 │
  │                                                  │
  │   ┌──────────┐     ┌──────────────────────┐     │
  │   │  Ingress │────►│  FST2SMS Service      │     │
  │   │  (NGINX) │     │  (2+ Replicas)        │     │
  │   └──────────┘     └──────┬───────────────┘     │
  │                           │                      │
  │              ┌────────────┼───────────┐          │
  │              ▼            ▼           ▼          │
  │        ┌──────────┐ ┌─────────┐ ┌────────┐      │
  │        │  Redis   │ │  Vault  │ │ML Svc  │      │
  │        │ Pub/Sub  │ │(Secrets)│ │(Mono)  │      │
  │        └────┬─────┘ └─────────┘ └────────┘      │
  │             │ WebSocket                          │
  │      ┌──────▼──────────┐                        │
  │      │ Other Servers / │                        │
  │      │    Clients      │                        │
  │      └─────────────────┘                        │
  └─────────────────────────────────────────────────┘
                    │
          ┌─────────▼───────────┐
          │ Grafana + Micrometer │
          └─────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Messaging Bus | Redis Pub/Sub |
| Real-Time Transport | WebSocket |
| Non-Blocking Runtime | Project Reactor (Mono / Flux) |
| Containerization | Docker |
| Orchestration | Kubernetes |
| Infrastructure as Code | Terraform |
| Secrets | HashiCorp Vault (via Terraform provider) |
| Observability | Micrometer + Grafana + Prometheus |
| Authentication | OAuth 2.0 |
| Resilience | Resilience4j Rate Limiter |
| ML Integration | Reactive HTTP via Mono with bounded retry |

---

## Infrastructure — Terraform + Docker (Redis)

Redis runs as a Docker container. Terraform manages the full container lifecycle — image pull, port binding, restart policy, and networking. No manual docker run commands are needed; everything is declared in Terraform HCL and applied via terraform apply. This keeps Redis provisioning reproducible and version-controlled alongside the rest of the infrastructure.

---

## Secrets Management — HashiCorp Vault via Terraform

All sensitive configuration — the SMS provider API key, OAuth client secret, and ML service URL — lives in HashiCorp Vault under a KV v2 secret path. The Vault instance itself is provisioned and configured via the Terraform Vault provider, so secret paths, policies, and access tokens are all declared as code.

At runtime the service fetches secrets from Vault on startup using Spring Cloud Vault. The Vault token is injected into the pod via a Kubernetes secret as an environment variable — it is never written into any config file or Docker image. If a secret rotates in Vault, a pod restart picks up the new value automatically.

---

## Kubernetes — Replication & Zero-Downtime

The service runs with a minimum of two replicas at all times. Deployments use a RollingUpdate strategy with maxUnavailable set to zero, meaning Kubernetes will never terminate an old pod until the replacement has passed its readiness check. This guarantees that traffic is always served during deploys.

The readiness probe hits /health on each pod. Only pods that return healthy are added to the Service endpoints and receive traffic. A pod that fails its liveness probe is restarted automatically without human intervention.

---

## Redis Pub/Sub + WebSocket

Redis Pub/Sub is the messaging backbone. When a message needs to be delivered it is published to a Redis channel. All service replicas and any connected WebSocket clients are subscribed to that channel and receive the message in real time.

WebSocket connections are the outward-facing transport. A client or another server can connect via WebSocket and both publish messages into the Redis channel and receive messages published by any other party. This means the WebSocket layer is fully symmetric — any connected party can push to any other connected party through Redis, regardless of which replica they are connected to.

---

## Non-Blocking I/O with Mono

The entire request pipeline is built on Project Reactor using Mono. No thread ever blocks waiting for a network call — every operation (Redis, Vault, SMS provider HTTP, ML service HTTP) returns a Mono and execution continues on the event loop. This keeps thread usage low and makes the service efficient under high concurrency without needing a large thread pool, which matters given the constrained 250m CPU limit.

There are no block() calls anywhere in the codebase. Blocking calls to Vault or any other synchronous client are offloaded to a bounded elastic scheduler so the event loop is never stalled.

---

## ML Model Integration via Mono

The ML model (e.g. a message classifier or spam filter) is called reactively via a WebClient wrapped in a Mono. Retries are strictly bounded — a maximum of three attempts with exponential backoff. This prevents the ML service from becoming a cascading failure point.

If all retries are exhausted, a safe fallback prediction is returned so the SMS pipeline continues. The retry count is a hard limit, not a soft suggestion, so a degraded ML service cannot hold up the main flow indefinitely. Future 4xx and 5xx responses from the ML service are caught and routed through the central error handler described below.

---

## Health Check & IP-Based Security

The /health endpoint is used by Kubernetes readiness and liveness probes. It is also the only diagnostic endpoint exposed outside the cluster for admin use. Access to /health is restricted at the Spring Security layer to a single configured static IP address. Any request arriving from a different IP receives a 403 Forbidden response immediately — no authentication challenge, no redirect.

This means only the admin machine with that specific static IP can query the health endpoint directly. All other traffic must go through the normal OAuth-authenticated API paths.

---

## OAuth Authentication

All API endpoints require a valid OAuth 2.0 token. The service is configured as an OAuth 2.0 resource server. Users and upstream services authenticate against a configured OAuth provider (Keycloak, Google, or any standards-compliant issuer) and pass a bearer token with every request.

Token validation is handled reactively by Spring Security WebFlux — no blocking filter chains. The OAuth client secret is fetched from Vault at startup and never stored in application config files.

---

## Resilience — Rate Limiter

A Resilience4j rate limiter is applied at the service boundary to protect downstream systems (SMS provider, ML service) from being overwhelmed. When the rate limit is breached the caller receives a 429 Too Many Requests response immediately rather than queuing up requests that would time out or cascade.

The rate limiter is configured with sensible defaults for the expected low-to-medium read load of this service and can be tuned per environment via application properties without code changes.

---

## Observability — Grafana + Micrometer

Micrometer is wired in as the metrics facade. It exposes a Prometheus-compatible scrape endpoint via Spring Actuator. Grafana scrapes Prometheus and provides dashboards.

The baseline metrics collected include JVM memory and CPU usage, HTTP request latency and status code distribution, Redis pub/sub message counts, rate limiter rejection counts, ML service call success and retry counts, and SMS send success and failure counts. These are enough to catch the most common operational issues — memory pressure, latency spikes, and downstream service degradation — without heavy instrumentation overhead.

---

## Error Handling — 4xx / 5xx

A global reactive exception handler is in place to intercept all 4xx and 5xx errors in a consistent way. Client errors (4xx) return a structured JSON body with an error code and human-readable message. Server errors (5xx) return a generic error response to the caller and log the full stack trace internally.

ML service failures and SMS provider failures are caught at the Mono pipeline level using onErrorResume and onErrorReturn, so they never bubble up as unhandled exceptions. The error handler is designed to be extended — adding handling for new error types in the future requires only adding a new case to the central handler without touching individual service classes.

---

## Resource Limits

The service is not read-heavy so resource limits are kept deliberately lean.

| Resource | Request | Limit |
|---|---|---|
| CPU | 250m | 250m |
| Memory | 512Mi | 512Mi |

Requests equal limits to give Kubernetes a Guaranteed QoS class, which means the pod will not be evicted under node memory pressure. If load increases significantly these values should be revisited before raising replica count.

---

## Getting Started

Prerequisites: Docker, Terraform, kubectl, a running Kubernetes cluster, a Vault instance, and an OAuth provider configured.

1. Clone the repository.
2. Copy .env.example to .env and fill in your Vault token, OAuth credentials, and admin static IP.
3. Run terraform init and terraform apply inside the terraform/ directory to provision Redis and configure Vault secrets.
4. Apply the Kubernetes manifests with kubectl apply -f k8s/.
5. Verify the deployment with kubectl rollout status deployment/fst2sms.
6. Hit /health from your configured admin static IP to confirm the service is up.
7. Import the provided Grafana dashboard JSON from observability/grafana-dashboard.json to start seeing metrics.

---

## Environment Variables

| Variable | Description | Source |
|---|---|---|
| VAULT_TOKEN | Token to authenticate with Vault | Kubernetes secret |
| VAULT_ADDR | Vault server address | Kubernetes config |
| ADMIN_STATIC_IP | IP allowed to access /health | Application config |
| OAUTH_ISSUER_URI | OAuth provider issuer URL | Application config |

All other sensitive values (API keys, OAuth client secret) are pulled from Vault at startup and do not need to be set as environment variables.

---

## Notes

- The service is stateless. All shared state (pub/sub, session data) lives in Redis. Any replica can handle any request.
- Vault token rotation requires a pod restart to pick up the new token. A future improvement is to use Vault Agent sidecar for automatic token renewal.
- The ML service is treated as an optional enhancement, not a hard dependency. The service degrades gracefully if the ML service is down.
- WebSocket connections are sticky per replica at the load balancer level. Redis Pub/Sub ensures messages published on one replica's WebSocket reach clients connected to any other replica.
