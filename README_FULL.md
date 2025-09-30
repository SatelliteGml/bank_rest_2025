# 🏦 Bank Cards Management System

Java Spring Boot + PostgreSQL + Docker

Полнофункциональное REST API для управления банковскими картами с JWT аутентификацией, ролевой системой доступа и шифрованием данных.

---

## 🎯 Основные возможности

### Для администратора
- Создание, редактирование и удаление карт
- Блокировка и активация карт
- Управление пользователями
- Просмотр всех карт в системе

### Для пользователя
- Просмотр своих карт с пагинацией
- Поиск карт по номеру
- Просмотр баланса карты
- Переводы между своими картами

### Системные возможности
- Автоматическая генерация номеров карт по алгоритму Луна
- Маскирование номеров карт в API ответах
- Шифрование чувствительных данных (номера карт, CVV)
- Валидация данных и обработка ошибок
- Логирование операций
- Миграции базы данных через Liquibase

---

## 🛠 Технологический стек

- **Java 17+**, **Spring Boot 3.5.6**
- **Spring Security**, JWT
- **Spring Data JPA**, PostgreSQL 17, Hibernate
- **Liquibase** для миграций
- **Docker & Docker Compose**, Maven, Lombok
- **JUnit 5**, Mockito, MockMvc для тестирования

---

## 🏗 Архитектура

- **Controller Layer** – REST API эндпоинты  
- **Service Layer** – бизнес-логика  
- **Repository Layer** – доступ к данным  
- **Entity Layer** – модели данных  
- **Security Layer** – аутентификация и авторизация  
- **Utility Layer** – вспомогательные функции

---

## 🚀 Быстрый старт

### Предварительные требования
- Java 17+
- Maven 3.6+
- Docker и Docker Compose

### Клонирование репозитория
```bash
git clone https://github.com/SatelliteGml/bank_rest_2025.git
cd cd bank_rest_2025
```

### Настройка окружения
Создайте файл `.env` в корне проекта:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bankcards_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SERVER_PORT=8080
JWT_SECRET=your-secret-key #85cd200f70bc9802fea817ff6ccd46722bfa3201f32560430154c6cafa940de2 for example
JWT_EXPIRATION=3600000
```

### Запуск с Docker Compose
```bash
docker-compose up -d
docker-compose logs -f app
```

### Запуск в режиме разработки
```bash
docker-compose up -d db
mvn clean package
mvn spring-boot:run
```

### Проверка
- API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
- Health Check: `http://localhost:8080/api/actuator/health` опционально

---

## 📚 API Документация

### Аутентификация
- `POST /api/auth/register` – регистрация
- `POST /api/auth/login` – вход в систему

### Управление картами
- `GET /api/admin/cards` – получить все карты (ADMIN)
- `POST /api/admin/cards` – создать карту (ADMIN)
- `GET /api/admin/cards/{id}` – карта по ID (ADMIN)
- `PUT /api/admin/cards/{id}/block` – блокировка карты (ADMIN)
- `PUT /api/admin/cards/{id}/activate` – активация карты (ADMIN)
- `DELETE /api/admin/cards/{id}` – удалить карту (ADMIN)

### Карты пользователя
- `GET /api/users/my` – свои карты
- `GET /api/users/paginated` – карты с пагинацией
- `GET /api/users/{id}/balance` – баланс карты
- `GET /api/users/{userId}/balances` – балансы пользователя

### Переводы
- `POST /api/transfers/own` – перевод между своими картами
- `POST /api/transfers/external` – перевод на внешнюю карту

---

## 🧪 Тестирование
- Запуск всех тестов: `mvn test`
- Конкретный класс тестов: `mvn test -Dtest=CardServiceImplTest`
- Покрытие: ~85%

---

## 🔒 Безопасность
- JWT для аутентификации
- Ролевой доступ (ADMIN, USER)
- BCrypt для хэширования паролей
- AES шифрование для номеров карт и CVV
- Маскирование номеров в API ответах

---

## 🐳 Развертывание (Docker)
```bash
docker build -t bank-cards-app:latest .
docker run -d \
  --name bank-cards-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/bankcards_db \
  -e SPRING_DATASOURCE_USERNAME=your-username \
  -e SPRING_DATASOURCE_PASSWORD=your-password \
  -e JWT_SECRET=your-production-secret-key \
  bank-cards-app:latest
```

