package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facultyService.createFaculty(faculty));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.editFaculty(faculty));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<Collection<Faculty>> filterByColor(@RequestParam String color) {
        return ResponseEntity.ok(facultyService.filterByColor(color));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<Faculty>> searchByNameOrColor(@RequestParam String query) {
        return ResponseEntity.ok(facultyService.findByNameOrColor(query));
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<Collection<Student>> getStudentsOfFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.findStudentsOfFaculty(id));
    }
}
