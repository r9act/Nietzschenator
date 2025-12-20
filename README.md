# RedsecBot & Nietzschenator

Event-driven Discord-бот с асинхронным обогащением данных через Kafka и LLM (GigaChat).

---

## Общее описание

Проект состоит из **двух независимых сервисов**, связанных через Kafka:

- **RedsecBot** — Discord-бот, который публикует события о статистике игроков и отправляет сообщения в Discord
- **Nietzschenator** — асинхронный сервис обогащения, который обрабатывает события, обращается к GigaChat и возвращает текстовую интерпретацию

Система построена на принципах **асинхронности**, **event-driven архитектуры** и **слабой связанности**.  
Kafka используется как **шина событий**, а не как RPC.

---

## Компоненты

### RedsecBot
- Discord-бот (JDA)
- Producer Kafka (`stats.ready`)
- Consumer Kafka (`stats.enriched`)
- In-memory registry для correlationId → Discord hook
- Отправляет **два сообщения** в Discord:
  1. Сразу — embed со статистикой
  2. Позже — текст от LLM

---

### Nietzschenator
- Stateless сервис
- Consumer Kafka (`stats.ready`)
- HTTP-клиент для GigaChat REST API
- Producer Kafka (`stats.enriched`)
- Не содержит логики Discord

---
