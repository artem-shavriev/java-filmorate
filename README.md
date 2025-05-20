
# Filmorate - сервис для оценки фильмов

[![Java](https://img.shields.io/badge/Java-21-blue)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![H2](https://img.shields.io/badge/H2-Database-lightgrey)](https://www.h2database.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9-red)](https://maven.apache.org/)

Backend-сервис для работы с фильмами и пользовательскими оценками с системой рекомендаций.

## 📌 Основные возможности

- Управление фильмами (добавление, обновление, поиск)
- Система пользовательских оценок (лайков)
- Топ-5 популярных фильмов
- Рекомендации для пользователей
- Управление друзьями (добавление, удаление, список общих друзей)

## 🛠 Технологический стек

### Основные технологии
- **Java 21**
- **Spring Boot 3.2**
- **Spring Data JDBC**
- **PostgreSQL 16**
- **H2**
- **Maven 3.9**

## 🚀 Запуск проекта

1. Клонировать репозиторий:
   git clone https://github.com/artem-shavriev/java-filmorate.git
2. Настроить базу данных:
   -Создать БД
   -Настроить подключение в application.properties

3. Собрать проект:
   mvn clean package
4. Запустить приложение:
   java -jar target/filmorate-1.0.jar

## 🎬 Filmorate API Endpoints

### 🎥 Films Controller

#### 📝 Create/Update Films
| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/films` | Create new film | `{"name": "Inception", "description": "A thief...", "releaseDate": "2010-07-16", "duration": 148, "mpa": {"id": 3}}` |
| `PUT` | `/films` | Update existing film | Same as POST |

#### 🔍 Get Films
| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| `GET` | `/films` | Get all films | - |
| `GET` | `/films/{id}` | Get film by ID | - |
| `GET` | `/films/popular` | Get top popular films | `count=10` (default) |

#### ❤️ Likes
| Method | Endpoint | Description |
|--------|----------|-------------|
| `PUT` | `/films/{id}/like/{userId}` | Add like to film |
| `DELETE` | `/films/{id}/like/{userId}` | Remove like from film |

### 👥 Users Controller

##### 📝 Create/Update Users
| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/users` | Create new user | `{"email": "user@mail.com", "login": "user123", "name": "John", "birthday": "1990-01-01"}` |
| `PUT` | `/users` | Update user | Same as POST |

##### 🔍 Get Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/users` | Get all users |
| `DELETE` | `/users/{id}` | Remove user|
| `GET` | `/users/{id}` | Get user by ID |
| `GET` | `/users/{id}/feed` | Get users feed |
| `GET` | `/users/{id}/recommendations` | Get users recommendations |

##### 👫 Friends

| Method | Endpoint | Description |
|--------|----------|-------------|
| `PUT` | `/users/{id}/friends/{friendId}` | Add friend |
| `DELETE` | `/users/{id}/friends/{friendId}` | Remove friend |
| `GET` | `/users/{id}/friends` | Get user's friends |
| `GET` | `/users/{id}/friends/common/{otherId}` | Get common friends |

###  Reviews Controller
##### 📝 Basic operations with reviews

| Метод  | Эндпоинт               | Описание                             | Тело запроса                                                                 |
|--------|-------------------------|--------------------------------------|------------------------------------------------------------------------------|
| `POST` | `/reviews`              | Создать новый отзыв                  | `{"content": "Текст отзыва", "isPositive": true, "userId": 1, "filmId": 1}` |
| `PUT`  | `/reviews`              | Обновить существующий отзыв          | `{"id": 1, "content": "Новый текст", "isPositive": false}`                  |
| `GET`  | `/reviews/{id}`         | Получить отзыв по ID                 | -                                                                           |
| `DELETE` | `/reviews/{id}`       | Удалить отзыв                        | -                                                                           |


##### 🔍 Get reviews

| Метод  | Эндпоинт| Описание|Параметры запроса|
|--------|----------------|-------------------------------------------|--------------------------------------------|
| `GET`  | `/reviews`     | Получить все отзывы или по фильму         | `filmId=123` (опционально) <br> `count=10` (лимит) |

##### 👍👎 Likes/dislikes

| Метод  | Эндпоинт                              | Описание                     |
|--------|---------------------------------------|------------------------------|
| `PUT`  | `/reviews/{reviewId}/like/{userId}`    | Поставить лайк отзыву        |
| `PUT`  | `/reviews/{reviewId}/dislike/{userId}` | Поставить дизлайк отзыву     |
| `DELETE` | `/reviews/{reviewId}/like/{userId}`   | Удалить лайк                 |
| `DELETE` | `/reviews/{reviewId}/dislike/{userId}`| Удалить дизлайк              |

## 🎬 Director Controller

##### 📋 Basic operations with directors

| Метод  | Эндпоинт            | Описание                            | Тело запроса                                                                 |
|--------|----------------------|-------------------------------------|------------------------------------------------------------------------------|
| `GET`  | `/directors`         | Получить всех режиссеров            | -                                                                           |
| `GET`  | `/directors/{id}`    | Получить режиссера по ID            | -                                                                           |
| `POST` | `/directors`         | Добавить нового режиссера           | `{"id": 1, "name": "Кристофер Нолан"}`                                     |
| `PUT`  | `/directors`         | Обновить данные режиссера           | `{"id": 1, "name": "Кристофер Нолан (обновлено)"}`                         |
| `DELETE` | `/directors/{id}`  | Удалить режиссера                   | -                                                                           |
## 🎭 Genre Controller

##### 📋 Basic operations with genres

| Метод  | Эндпоинт       | Описание                          | Параметры |
|--------|----------------|-----------------------------------|-----------|
| `GET`  | `/genres`      | Получить список всех жанров       | -         |
| `GET`  | `/genres/{id}` | Получить жанр по идентификатору   | `id`      |

## 🎬 MPA  Controller

##### 📋  Basic operations with MPA

| Метод  | Эндпоинт   | Описание                                      | Параметры |
|--------|------------|-----------------------------------------------|-----------|
| `GET`  | `/mpa`     | Получить список всех возрастных рейтингов     | -         |
| `GET`  | `/mpa/{id}`| Получить возрастной рейтинг по ID             | `id`      |

## 🔄 Status Codes
200 OK - Successful operation

201 Created - Resource created

400 Bad Request - Invalid parameters

404 Not Found - Resource not found

500 Internal Server Error - Server error

Диаграмма базы данных приложения:
![Filmorate data base image](/images/filmorateDataBaseImage.png)