# Hogwarts School

Учебный REST-сервис «Школа Хогвартс» на Spring Boot: студенты и факультеты хранятся в PostgreSQL через Spring Data JPA.

## Стек

- Java 17
- Spring Boot 3.2.3 (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`)
- PostgreSQL (драйвер `org.postgresql:postgresql`)
- Maven (wrapper `mvnw`)

## Структура

```
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

Модели размечены `@Entity`, идентификатор — `@Id` + `@GeneratedValue(strategy = IDENTITY)`.
Репозитории наследуют `JpaRepository<T, Long>`, поиск — производными методами (`findByAge`, `findByColor`).
Сервисы получают репозитории через конструктор (`@Autowired`), собственных хранилищ в памяти нет.

## Подготовка базы данных

В PostgreSQL должны существовать база `hogwarts` и пользователь `student`:

```sql
CREATE
DATABASE hogwarts;
CREATE
USER student WITH PASSWORD 'chocolatefrog';
GRANT ALL PRIVILEGES ON DATABASE
hogwarts TO student;
\c
hogwarts
GRANT ALL ON SCHEMA public TO student;  -- обязательно для PostgreSQL 15+
```

Параметры подключения — в `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hogwarts
spring.datasource.username=student
spring.datasource.password=chocolatefrog
spring.jpa.hibernate.ddl-auto=update
```

Таблицы создаются автоматически при старте приложения (`ddl-auto=update`).

## Запуск

```bash
./mvnw spring-boot:run
```

Приложение поднимается на `http://localhost:8080`.

## API

Базовый URL: `http://localhost:8080`

### Студенты — `/student`

| Метод  | Путь                     | Тело запроса                       | Ответ                                     |
|--------|--------------------------|------------------------------------|-------------------------------------------|
| POST   | `/student`               | `{"name":"Harry","age":11}`        | `201` + созданный студент с `id`          |
| GET    | `/student/{id}`          | —                                  | `200` + студент, либо `404`               |
| PUT    | `/student`               | `{"id":1,"name":"Harry","age":12}` | `200` + обновлённый студент               |
| DELETE | `/student/{id}`          | —                                  | `200`                                     |
| GET    | `/student/filter?age=11` | —                                  | `200` + список студентов с этим возрастом |

### Факультеты — `/faculty`

| Метод  | Путь                        | Тело запроса                                 | Ответ                                  |
|--------|-----------------------------|----------------------------------------------|----------------------------------------|
| POST   | `/faculty`                  | `{"name":"Gryffindor","color":"red"}`        | `201` + созданный факультет с `id`     |
| GET    | `/faculty/{id}`             | —                                            | `200` + факультет, либо `404`          |
| PUT    | `/faculty`                  | `{"id":1,"name":"Gryffindor","color":"red"}` | `200` + обновлённый факультет          |
| DELETE | `/faculty/{id}`             | —                                            | `200`                                  |
| GET    | `/faculty/filter?color=red` | —                                            | `200` + список факультетов этого цвета |

### Особенности

- В `PUT` идентификатор обязателен в теле запроса; при несуществующем `id` возвращается `200` с пустым телом.
- `POST` игнорирует переданный `id` и всегда создаёт новую запись.
- Фильтры живут на отдельных путях `/filter`, а не на корне коллекции.
