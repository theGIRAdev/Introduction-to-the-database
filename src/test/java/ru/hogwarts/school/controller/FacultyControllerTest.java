package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateFaculty() {
        Faculty faculty = new Faculty(null, "TestFaculty", "blue");

        ResponseEntity<Faculty> response = restTemplate.postForEntity("/faculty", faculty, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("TestFaculty");
        assertThat(response.getBody().getColor()).isEqualTo("blue");
    }

    @Test
    void shouldReturnFacultyById() {
        Faculty created = restTemplate.postForObject("/faculty", new Faculty(null, "GetFaculty", "green"), Faculty.class);

        ResponseEntity<Faculty> response =
                restTemplate.getForEntity("/faculty/{id}", Faculty.class, created.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("GetFaculty");
    }

    @Test
    void shouldReturn404ForUnknownFaculty() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity("/faculty/999999", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateFaculty() {
        Faculty created = restTemplate.postForObject("/faculty", new Faculty(null, "BeforeEdit", "grey"), Faculty.class);
        created.setName("AfterEdit");
        created.setColor("white");

        restTemplate.put("/faculty", created);

        ResponseEntity<Faculty> response =
                restTemplate.getForEntity("/faculty/{id}", Faculty.class, created.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("AfterEdit");
        assertThat(response.getBody().getColor()).isEqualTo("white");
    }

    @Test
    void shouldDeleteFaculty() {
        Faculty created = restTemplate.postForObject("/faculty", new Faculty(null, "ToDeleteFaculty", "black"), Faculty.class);

        restTemplate.delete("/faculty/{id}", created.getId());

        ResponseEntity<Faculty> response =
                restTemplate.getForEntity("/faculty/{id}", Faculty.class, created.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldSearchFacultyByNameOrColorIgnoringCase() {
        restTemplate.postForObject("/faculty", new Faculty(null, "Ravenclaw", "bronze"), Faculty.class);

        ResponseEntity<Faculty[]> byName = restTemplate.getForEntity("/faculty/search?query=raven", Faculty[].class);
        ResponseEntity<Faculty[]> byColor = restTemplate.getForEntity("/faculty/search?query=BRONZE", Faculty[].class);

        assertThat(byName.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(byName.getBody()).isNotEmpty();

        assertThat(byColor.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(byColor.getBody()).isNotEmpty();
    }
}