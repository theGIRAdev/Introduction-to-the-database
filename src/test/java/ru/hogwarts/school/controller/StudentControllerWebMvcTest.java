package ru.hogwarts.school.controller;

import ru.hogwarts.school.model.Faculty;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    void shouldReturnStudentById() throws Exception {
        when(studentService.findStudent(1L)).thenReturn(new Student(1L, "Harry", 11));

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry"));
    }

    @Test
    void shouldReturn404WhenStudentNotFound() throws Exception {
        when(studentService.findStudent(999L)).thenReturn(null);

        mockMvc.perform(get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateStudent() throws Exception {
        Student request = new Student(null, "NewStudent", 12);
        Student saved = new Student(10L, "NewStudent", 12);
        when(studentService.createStudent(any(Student.class))).thenReturn(saved);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("NewStudent"));
    }

    @Test
    void shouldUpdateStudent() throws Exception {
        Student request = new Student(5L, "Updated", 20);
        when(studentService.editStudent(any(Student.class))).thenReturn(request);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.age").value(20));
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        mockMvc.perform(delete("/student/5"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(5L);
    }

    @Test
    void shouldFilterStudentsByAgeBetween() throws Exception {
        when(studentService.findByAgeBetween(10, 20))
                .thenReturn(List.of(new Student(1L, "Young", 12), new Student(2L, "Older", 18)));

        mockMvc.perform(get("/student")
                        .param("min", "10")
                        .param("max", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Young"))
                .andExpect(jsonPath("$[1].name").value("Older"));
    }
    @Test
    void shouldFilterStudentsByAge() throws Exception {
        when(studentService.filterByAge(12))
                .thenReturn(List.of(new Student(3L, "Twelve", 12)));

        mockMvc.perform(get("/student/filter").param("age", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].age").value(12));
    }

    @Test
    void shouldReturnFacultyOfStudent() throws Exception {
        when(studentService.findFacultyOfStudent(1L))
                .thenReturn(new Faculty(2L, "Gryffindor", "red"));

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }

}