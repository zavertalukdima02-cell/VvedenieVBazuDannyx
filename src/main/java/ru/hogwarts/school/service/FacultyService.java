package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.hogwarts.model.Faculty;
import org.example.hogwarts.model.Student;
import org.example.hogwarts.repository.FacultyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty create(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        logger.debug("Creating faculty with name = {}", faculty.getName());
        return facultyRepository.save(faculty);
    }

    public Optional<Faculty> getById(Long id) {
        logger.info("Was invoked method for get faculty by id");
        logger.debug("Requested faculty id = {}", id);
        Optional<Faculty> faculty = facultyRepository.findById(id);
        if (faculty.isEmpty()) {
            logger.warn("There is not faculty with id = {}", id);
        }
        return faculty;
    }

    public List<Faculty> getAll() {
        logger.info("Was invoked method for get all faculties");
        List<Faculty> faculties = facultyRepository.findAll();
        logger.debug("Found {} faculties", faculties.size());
        return faculties;
    }

    public List<Faculty> findByNameOrColorIgnoreCase(String value) {
        logger.info("Was invoked method for find faculty by name or color");
        logger.debug("Search value = {}", value);
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(value, value);
    }

    public Faculty update(Long id, Faculty faculty) {
        logger.info("Was invoked method for update faculty");
        logger.debug("Updating faculty with id = {}", id);
        if (!facultyRepository.existsById(id)) {
            logger.warn("There is not faculty with id = {}", id);
            return null;
        }
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }

    public boolean delete(Long id) {
        logger.info("Was invoked method for delete faculty");
        logger.debug("Deleting faculty with id = {}", id);
        if (!facultyRepository.existsById(id)) {
            logger.warn("There is not faculty with id = {}", id);
            return false;
        }
        facultyRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsOfFaculty(Long facultyId) {
        logger.info("Was invoked method for get students of faculty");
        logger.debug("Requested faculty id = {}", facultyId);
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> {
                    logger.error("There is not faculty with id = {}", facultyId);
                    return new EntityNotFoundException("Faculty not found: " + facultyId);
                });
        List<Student> students = faculty.getStudents();
        logger.debug("Found {} students for faculty id = {}", students.size(), facultyId);
        return students;
    }

    public String getLongestFacultyName() {
        logger.info("Was invoked method for get longest faculty name");
        String longestName = facultyRepository.findAll().parallelStream()
                .map(Faculty::getName)
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
        logger.debug("Longest faculty name = {}", longestName);
        return longestName;
    }
}