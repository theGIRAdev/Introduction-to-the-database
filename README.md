# Hogwarts School

Учебный REST-сервис «Школа Хогвартс» на Spring Boot: студенты и факультеты хранятся в PostgreSQL через Spring Data JPA.

## Стек

- Java 17
- Spring Boot 3.2.3 (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`)
- PostgreSQL (драйвер `org.postgresql:postgresql`)
- Maven (wrapper `mvnw`)

## Структура

```
scripts.sql                     — SQL-запросы к таблице student
src/main/java/ru/hogwarts/school
├── HogwartsSchoolApplication.java
├── controller
│   ├── FacultyController.java
│   └── StudentController.java
├── model
│   ├── Faculty.java
│   └── Student.java
├── repository
│   ├── FacultyRepository.java
│   └── StudentRepository.java
└── service
    ├── FacultyService.java
    └── StudentService.java
```

## Модель данных

Между студентом и факультетом — связь «многие к одному»:

- `Student.faculty` — `@ManyToOne` + `@JoinColumn(name = "faculty_id")`, владеющая сторона, внешний ключ хранится в таблице `student`;
- `Faculty.students` — `@OneToMany(mappedBy = "faculty")`, обратная сторона, помечена `@JsonIgnore`, чтобы не возникало циклической сериализации в JSON.

Идентификатор — `@Id` + `@GeneratedValue(strategy = IDENTITY)`.

## Подготовка базы данных

В PostgreSQL должны существовать база `hogwarts` и пользователь `student`:

```sql
CREATE DATABASE hogwarts;
CREATE USER student WITH PASSWORD 'chocolatefrog';
GRANT ALL PRIVILEGES ON DATABASE hogwarts TO student;
\c hogwarts
GRANT ALL ON SCHEMA public TO student;  -- обязательно для PostgreSQL 15+
```

Параметры подключения — в `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hogwarts
spring.datasource.username=student
spring.datasource.password=chocolatefrog
spring.jpa.hibernate.ddl-auto=update
```

Таблицы и колонка `faculty_id` создаются автоматически при старте приложения (`ddl-auto=update`).

## Запуск

```bash
./mvnw spring-boot:run
```

Приложение поднимается на `http://localhost:8080`.

## API

Базовый URL: `http://localhost:8080`

### Студенты — `/student`

| Метод | Путь | Тело запроса | Ответ |
|---|---|---|---|
| POST | `/student` | `{"name":"Harry","age":11,"faculty":{"id":1}}` | `201` + созданный студент с `id` |
| GET | `/student/{id}` | — | `200` + студент, либо `404` |
| PUT | `/student` | `{"id":1,"name":"Harry","age":12}` | `200` + обновлённый студент |
| DELETE | `/student/{id}` | — | `200` |
| GET | `/student/filter?age=11` | — | `200` + список студентов с этим возрастом |
| GET | `/student?min=10&max=20` | — | `200` + студенты с возрастом в диапазоне (включительно) |
| GET | `/student/{id}/faculty` | — | `200` + факультет студента, либо `404` |

### Факультеты — `/faculty`

| Метод | Путь | Тело запроса | Ответ |
|---|---|---|---|
| POST | `/faculty` | `{"name":"Gryffindor","color":"red"}` | `201` + созданный факультет с `id` |
| GET | `/faculty/{id}` | — | `200` + факультет, либо `404` |
| PUT | `/faculty` | `{"id":1,"name":"Gryffindor","color":"red"}` | `200` + обновлённый факультет |
| DELETE | `/faculty/{id}` | — | `200` |
| GET | `/faculty/filter?color=red` | — | `200` + факультеты этого цвета |
| GET | `/faculty/search?query=gryf` | — | `200` + поиск по имени **или** цвету, без учёта регистра |
| GET | `/faculty/{id}/students` | — | `200` + список студентов факультета |

### Особенности

- В `PUT` идентификатор обязателен в теле запроса; при несуществующем `id` возвращается `200` с пустым телом.
- `POST` игнорирует переданный `id` и всегда создаёт новую запись.
- Студент привязывается к факультету передачей `{"faculty":{"id":1}}` в теле запроса.
- Поиск студентов по диапазону возрастов и поиск факультета по имени/цвету построены на производных методах репозитория: `findByAgeBetween` и `findByNameContainingIgnoreCaseOrColorContainingIgnoreCase`.

## SQL

В корне лежит `scripts.sql` — пять запросов к таблице `student`:

1. студенты с возрастом в диапазоне `BETWEEN 10 AND 20`;
2. только имена студентов (проекция);
3. студенты, в имени которых есть буква `o` (`ILIKE` — регистронезависимый поиск);
4. студенты, у которых возраст меньше идентификатора (сравнение двух колонок);
5. студенты, отсортированные по возрасту (`ORDER BY`).

Выполнить можно в pgAdmin (Query Tool на базе `hogwarts`) или в любом другом SQL-клиенте.
