package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultyService facultyService;

    @Test
    void shouldReturnFacultyById() throws Exception {
        when(facultyService.findFaculty(1L)).thenReturn(new Faculty(1L, "Gryffindor", "red"));

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    @Test
    void shouldReturn404ForUnknownFaculty() throws Exception {
        when(facultyService.findFaculty(999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateFaculty() throws Exception {
        Faculty request = new Faculty(null, "NewFaculty", "yellow");
        Faculty saved = new Faculty(7L, "NewFaculty", "yellow");
        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(saved);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.color").value("yellow"));
    }

    @Test
    void shouldUpdateFaculty() throws Exception {
        Faculty request = new Faculty(2L, "Renamed", "purple");
        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(request);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed"))
                .andExpect(jsonPath("$.color").value("purple"));
    }

    @Test
    void shouldDeleteFaculty() throws Exception {
        mockMvc.perform(delete("/faculty/3"))
                .andExpect(status().isOk());

        verify(facultyService, times(1)).deleteFaculty(3L);
    }

    @Test
    void shouldSearchFacultyByNameOrColor() throws Exception {
        when(facultyService.findByNameOrColor("raven"))
                .thenReturn(List.of(new Faculty(4L, "Ravenclaw", "bronze")));

        mockMvc.perform(get("/faculty/search").param("query", "raven"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Ravenclaw"));
    }

    @Test
    void shouldReturnStudentsOfFaculty() throws Exception {
        when(facultyService.findStudentsOfFaculty(1L))
                .thenReturn(List.of(new Student(1L, "Harry", 11)));

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Harry"));
    }
}
