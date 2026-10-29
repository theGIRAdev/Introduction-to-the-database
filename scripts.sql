-- Студенты в возрасте от 10 до 20
SELECT * FROM student WHERE age BETWEEN 10 AND 20;

-- Только имена студентов
SELECT name FROM student;

-- Студенты, в имени которых есть буква o
SELECT * FROM student WHERE name ILIKE '%o%';

-- Студенты, у которых возраст меньше id
SELECT * FROM student WHERE age < id;

-- Студенты, отсортированные по возрасту
SELECT * FROM student ORDER BY age;