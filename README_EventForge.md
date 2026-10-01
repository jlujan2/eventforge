# ⚡ EventForge — Kafka & Distributed Systems Lab

## Objetivo

Construir un sistema event-driven realista para aprender:

- Apache Kafka
- Event-driven architecture
- Producers y consumers
- Topics y partitions
- Consumer groups
- Offsets y consumer lag
- Retries y Dead Letter Topics
- Idempotency
- Delivery semantics
- Schema evolution
- Observability
- Scaling
- System design
- Kafka en AWS usando Amazon MSK

La idea no es solo aprender comandos de Kafka, sino entender cómo se usa en sistemas reales y poder explicarlo en entrevistas.

---

## Arquitectura inicial

El sistema crecerá hasta algo similar a esto:

```text
                 ┌─────────────────┐
POST /orders --->│  Order Service  │
                 └────────┬────────┘
                          │
                    order.created
                          │
                          ▼
                     ┌─────────┐
                     │  Kafka  │
                     └────┬────┘
                          │
            ┌─────────────┼─────────────┐
            ▼             ▼             ▼
      Inventory       Payment       Analytics
       Service         Service        Service
```

Al principio NO construiremos todo.

Empezaremos solamente con:

```text
Producer
   |
   v
Kafka topic
   |
   v
Consumer
```

---

# Phase 1 — Local Kafka Setup

## 1. Crear el proyecto

Crear una carpeta:

```bash
mkdir eventforge
cd eventforge
```

Estructura inicial:

```text
eventforge/
└── docker-compose.yml
```

---

## 2. Crear `docker-compose.yml`

```yaml
services:
  kafka:
    image: apache/kafka:latest
    container_name: eventforge-kafka
    ports:
      - "9092:9092"
```

---

## 3. Levantar Kafka

```bash
docker compose up -d
```

Verificar:

```bash
docker ps
```

Debería aparecer algo similar a:

```text
eventforge-kafka
```

con estado:

```text
Up
```

---

# Phase 2 — Nuestro primer Kafka Topic

Entrar al contenedor:

```bash
docker exec -it eventforge-kafka bash
```

Crear un topic:

```bash
/opt/kafka/bin/kafka-topics.sh \
  --create \
  --topic order.created \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

Verificar los topics:

```bash
/opt/kafka/bin/kafka-topics.sh \
  --list \
  --bootstrap-server localhost:9092
```

Esperamos ver:

```text
order.created
```

---

# Phase 3 — Producer y Consumer

## Consumer

Abrir una terminal:

```bash
docker exec -it eventforge-kafka bash
```

Luego:

```bash
/opt/kafka/bin/kafka-console-consumer.sh \
  --topic order.created \
  --bootstrap-server localhost:9092 \
  --from-beginning
```

El consumer se queda esperando mensajes.

---

## Producer

Abrir otra terminal:

```bash
docker exec -it eventforge-kafka bash
```

Luego:

```bash
/opt/kafka/bin/kafka-console-producer.sh \
  --topic order.created \
  --bootstrap-server localhost:9092
```

Enviar:

```json
{"orderId":1,"product":"keyboard","quantity":2}
```

El mensaje debe aparecer en el consumer.

Flujo:

```text
Producer
   |
   v
Kafka
   |
   v
order.created
   |
   v
Partition
   |
   v
Consumer
```

---

# Conceptos aprendidos hasta ahora

## Producer

Aplicación que envía mensajes a Kafka.

Ejemplo:

```text
Order Service
```

publica:

```text
order.created
```

---

## Topic

Un stream lógico de eventos.

Ejemplo:

```text
order.created
```

puede contener:

```text
Order 1
Order 2
Order 3
Order 4
```

---

## Partition

Una división interna del topic.

Ejemplo:

```text
order.created

Partition 0
Partition 1
Partition 2
```

Las partitions permiten principalmente:

```text
parallelism
```

No son servidores ni microservicios.

---

## Consumer

Aplicación que lee eventos de Kafka.

Ejemplo:

```text
Inventory Service
```

puede consumir:

```text
order.created
```

para reservar stock.

---

# Phase 4 — Consumer Groups

Crear dos consumers usando el mismo group:

```bash
/opt/kafka/bin/kafka-console-consumer.sh \
  --topic order.created \
  --bootstrap-server localhost:9092 \
  --group inventory-group
```

Ejecutar ese comando en dos terminales.

Entonces tendremos:

```text
inventory-group

Consumer A
Consumer B
```

Kafka distribuye las partitions entre ellos.

Con:

```text
3 partitions
2 consumers
```

podríamos tener:

```text
Consumer A
  ├── Partition 0
  └── Partition 2

Consumer B
  └── Partition 1
```

Regla importante:

```text
una partition
solo puede ser procesada
por un consumer del mismo group
al mismo tiempo
```

Pero un consumer puede leer varias partitions.

---

# Relación partitions / consumers

Con:

```text
3 partitions
3 consumers
```

podemos tener:

```text
Consumer A -> Partition 0
Consumer B -> Partition 1
Consumer C -> Partition 2
```

Pero si tenemos:

```text
3 partitions
5 consumers
```

entonces:

```text
Consumer A -> Partition 0
Consumer B -> Partition 1
Consumer C -> Partition 2
Consumer D -> idle
Consumer E -> idle
```

Por eso:

```text
max useful consumers per consumer group
≈ number of partitions
```

---

# Próximo paso

Nuestro siguiente experimento será:

```text
Consumer Groups
      ↓
Offsets
      ↓
Consumer Lag
```

Usaremos:

```bash
/opt/kafka/bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --group inventory-group
```

y veremos:

```text
TOPIC
PARTITION
CURRENT-OFFSET
LOG-END-OFFSET
LAG
```

Después construiremos el primer servicio real usando:

```text
Java
Spring Boot
Spring Kafka
```

y reemplazaremos:

```text
console producer
console consumer
```

por:

```text
Order Service
Inventory Service
```

Ahí EventForge empieza a convertirse en un proyecto de verdad 😎.
