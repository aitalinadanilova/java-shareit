# ShareIt — Сервис для шеринга и бронирования вещей

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

**ShareIt** — это микросервисная платформа, позволяющая пользователям делиться вещами, брать их в аренду и оставлять отзывы после использования. Система обеспечивает полный цикл бронирования: от создания запроса на вещь до подтверждения аренды владельцем.

## 🛠 Технологический стек
*   **Язык программирования:** Java 17
*   **Фреймворк:** Spring Boot 3 (Spring Data JPA, Spring Web)
*   **База данных:** PostgreSQL (Production), H2 (Testing)
*   **Контейнеризация:** Docker & Docker Compose (Multi-module)
*   **Тестирование:** JUnit 5, Mockito, AssertJ
*   **Сборка:** Maven

## 🚀 Реализованная функциональность
Мною была спроектирована и реализована логика взаимодействия между модулями и система бронирования.

**Ключевые возможности:**
*   **Микросервисная архитектура:** Проект разделен на `shareit-server` (бизнес-логика) и `shareit-gateway` (валидация входящих данных), что обеспечивает масштабируемость.
*   **Система бронирования:** Реализована сложная логика статусов бронирования (WAITING, APPROVED, REJECTED, CANCELED).
*   **Запросы на вещи (Item Requests):** Возможность создавать запросы на отсутствующие в системе вещи; уведомление пользователей при их появлении.
*   **Отзывы и комментарии:** Реализована возможность оставлять отзывы только после реального завершения аренды (защита от ложных комментариев).
*   **Оптимизация БД:** Настройка JPQL-запросов и постраничного вывода (Pagination) для эффективной работы с большими списками данных.

---

## Запуск проекта
```bash
mvn clean package
docker-compose up
