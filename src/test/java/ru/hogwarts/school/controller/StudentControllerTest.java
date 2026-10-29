package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Student;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateStudent() {
        Student student = new Student(null, "TestStudent", 15);

        ResponseEntity<Student> response = restTemplate.postForEntity("/student", student, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("TestStudent");
    }

    @Test
    void shouldReturnStudentById() {
        Student created = restTemplate.postForObject("/student", new Student(null, "GetById", 16), Student.class);

        ResponseEntity<Student> response =
                restTemplate.getForEntity("/student/{id}", Student.class, created.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("GetById");
    }

    @Test
    void shouldReturn404ForUnknownStudent() {
        ResponseEntity<Student> response = restTemplate.getForEntity("/student/999999", Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateStudent() {
        Student created = restTemplate.postForObject("/student", new Student(null, "BeforeUpdate", 17), Student.class);
        created.setName("AfterUpdate");
        created.setAge(18);

        restTemplate.put("/student", created);

        ResponseEntity<Student> response =
                restTemplate.getForEntity("/student/{id}", Student.class, created.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("AfterUpdate");
        assertThat(response.getBody().getAge()).isEqualTo(18);
    }

    @Test
    void shouldDeleteStudent() {
        Student created = restTemplate.postForObject("/student", new Student(null, "ToDelete", 19), Student.class);

        restTemplate.delete("/student/{id}", created.getId());

        ResponseEntity<Student> response =
                restTemplate.getForEntity("/student/{id}", Student.class, created.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFilterStudentsByAgeBetween() {
        restTemplate.postForObject("/student", new Student(null, "InRange", 12), Student.class);
        restTemplate.postForObject("/student", new Student(null, "OutOfRange", 40), Student.class);

        ResponseEntity<Student[]> response =
                restTemplate.getForEntity("/student?min=10&max=20", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
                .allSatisfy(student -> assertThat(student.getAge()).isBetween(10, 20));
    }
}