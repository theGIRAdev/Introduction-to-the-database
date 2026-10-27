package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(null);
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(Long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty) {
        if (faculty.getId() == null || !facultyRepository.existsById(faculty.getId())) {
            return null;
        }
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> filterByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> findByNameOrColor(String query) {
        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(query, query);
    }

    public Collection<Student> findStudentsOfFaculty(Long facultyId) {
        return studentRepository.findByFaculty_Id(facultyId);
    }
}
