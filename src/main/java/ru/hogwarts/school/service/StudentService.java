package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.hogwarts.model.Faculty;
import org.example.hogwarts.model.Student;
import org.example.hogwarts.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student create(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student with name = {}", student.getName());
        return studentRepository.save(student);
    }

    public Optional<Student> getById(Long id) {
        logger.info("Was invoked method for get student by id");
        logger.debug("Requested student id = {}", id);
        Optional<Student> student = studentRepository.findById(id);
        if (student.isEmpty()) {
            logger.warn("There is not student with id = {}", id);
        }
        return student;
    }

    public List<Student> getAll() {
        logger.info("Was invoked method for get all students");
        List<Student> students = studentRepository.findAll();
        logger.debug("Found {} students", students.size());
        return students;
    }

    public List<Student> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find students by age between");
        logger.debug("Age range: min = {}, max = {}", min, max);
        return studentRepository.findByAgeBetween(min, max);
    }

    public long getStudentsCount() {
        logger.info("Was invoked method for get students count");
        long count = studentRepository.getStudentsCount();
        logger.debug("Students count = {}", count);
        return count;
    }

    public Double getStudentsAverageAge() {
        logger.info("Was invoked method for get students average age");
        Double averageAge = studentRepository.getStudentsAverageAge();
        logger.debug("Students average age = {}", averageAge);
        return averageAge;
    }

    public List<Student> findLastFiveStudents() {
        logger.info("Was invoked method for find last five students");
        return studentRepository.findLastFiveStudents();
    }

    public Student update(Long id, Student student) {
        logger.info("Was invoked method for update student");
        logger.debug("Updating student with id = {}", id);
        if (!studentRepository.existsById(id)) {
            logger.warn("There is not student with id = {}", id);
            return null;
        }
        student.setId(id);
        return studentRepository.save(student);
    }

    public boolean delete(Long id) {
        logger.info("Was invoked method for delete student");
        logger.debug("Deleting student with id = {}", id);
        if (!studentRepository.existsById(id)) {
            logger.warn("There is not student with id = {}", id);
            return false;
        }
        studentRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public Faculty getFacultyOfStudent(Long studentId) {
        logger.info("Was invoked method for get faculty of student");
        logger.debug("Requested student id = {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("There is not student with id = {}", studentId);
                    return new EntityNotFoundException("Student not found: " + studentId);
                });
        Faculty faculty = student.getFaculty();
        if (faculty == null) {
            logger.warn("Student with id = {} has no faculty", studentId);
        }
        return faculty;
    }

    public List<String> getStudentNamesStartingWithA() {
        logger.info("Was invoked method for get student names starting with A");
        List<String> names = studentRepository.findAll().parallelStream()
                .map(Student::getName)
                .filter(Objects::nonNull)
                .filter(this::nameStartsWithA)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
        logger.debug("Found {} student names starting with A", names.size());
        return names;
    }

    public double getStudentsAverageAgeByStream() {
        logger.info("Was invoked method for get students average age by stream");
        double averageAge = studentRepository.findAll().parallelStream()
                .map(Student::getAge)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        logger.debug("Students average age by stream = {}", averageAge);
        return averageAge;
    }

    private boolean nameStartsWithA(String name) {
        if (name.isEmpty()) {
            return false;
        }
        char firstLetter = Character.toUpperCase(name.charAt(0));
        return firstLetter == 'A' || firstLetter == 'А';
    }
}