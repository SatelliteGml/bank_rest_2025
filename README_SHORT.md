# 🏦 Bank Cards Management System

Java Spring Boot + PostgreSQL + Docker

REST API для управления банковскими картами с JWT аутентификацией и шифрованием данных.

## Основные функции

- Администратор: управление картами и пользователями
- Пользователь: просмотр карт, балансов и переводы
- Автоматическая генерация и маскирование номеров карт
- Шифрование номеров карт и CVV

## Технологии

- Java 17+, Spring Boot 3.5.6
- Spring Security, JWT, Spring Data JPA, PostgreSQL 17, Hibernate
- Liquibase, Docker, Maven, Lombok
- JUnit 5, Mockito, MockMvc

## Быстрый старт

1. Клонировать репозиторий и перейти в папку:
```bash
git clone https://github.com/SatelliteGml/bank_rest_2025.git
cd cd bank_rest_2025
```
2. Настроить `.env`
3. Запустить Docker Compose: `docker-compose up -d`
4. Запустить приложение: `mvn clean package && mvn spring-boot:run`

## API и Swagger
- Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
- Основные эндпоинты: регистрация, логин, управление картами, переводы

## Тестирование
- Запуск тестов: `mvn test`
- Покрытие: ~85%

## Безопасность
- JWT + ролевой доступ
- BCrypt для паролей
- AES шифрование для карт и CVV

