# java-filmorate
Template repository for Filmorate project.  

Диаграмма базы данных приложения:
![Filmorate data base image](/images/filmorateDataBaseImage.png)

Примеры запросов для основных операций приложения:
- Создать пользователя (POST http://localhost:8080/users)
- Обновить пользователя (PUT http://localhost:8080/users)
- Получить всех пользовтелей (GET http://localhost:8080/users)
- Добавить в друзья (PUT http://localhost:8080/users/{id}/friends/{friendId})
- Удалить из друзей (DELETE http://localhost:8080/users/{id}/friends/{friendId})
- Получить всех друзей (GET http://localhost:8080/users/{id}/friends)
- Получить общих друзей (GET http://localhost:8080/users/{id}/friends/common/{otherId})
- Получить все фильмы (GET http://localhost:8080/films)
- Добавить фильм (POST http://localhost:8080/films)
- Обновить фильм (PUT  http://localhost:8080/films)
- Поставить лайк фильму (PUT http://localhost:8080/films/{id}/like/{userId})
- Удалить лайк (DELETE http://localhost:8080/films/{id}/like/{userId})
- Получить указанное число самых популярных фильмов (GET http://localhost:8080/films/popular?count={count})
- Получить 10 самых популярных фильмов (GET http://localhost:8080/films/popular)