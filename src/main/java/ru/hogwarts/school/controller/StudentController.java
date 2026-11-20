package ru.hogwarts.school.controller;

import org.example.hogwarts.model.Faculty;
import org.example.hogwarts.model.Student;
import org.example.hogwarts.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.create(student));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return studentService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Student> getAll() {
        return studentService.getAll();
    }

    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        Student updated = studentService.update(id, student);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = studentService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // Шаг 1.1: студенты по возрасту (min/max из query-параметров)
    @GetMapping(value = "/age", params = {"min", "max"})
    public List<Student> getStudentsByAgeBetween(@RequestParam int min, @RequestParam int max) {
        return studentService.findByAgeBetween(min, max);
    }

    @GetMapping("/count")
    public long getStudentsCount() {
        return studentService.getStudentsCount();
    }

    @GetMapping("/average-age")
    public Double getStudentsAverageAge() {
        return studentService.getStudentsAverageAge();
    }

    @GetMapping("/names-starting-with-a")
    public List<String> getStudentNamesStartingWithA() {
        return studentService.getStudentNamesStartingWithA();
    }

    @GetMapping("/average-age-stream")
    public double getStudentsAverageAgeByStream() {
        return studentService.getStudentsAverageAgeByStream();
    }

    @GetMapping("/last-five")
    public List<Student> getLastFiveStudents() {
        return studentService.findLastFiveStudents();
    }

    // Шаг 4.1: факультет конкретного студента
    @GetMapping("/{id:[0-9]+}/faculty")
    public ResponseEntity<Faculty> getFacultyOfStudent(@PathVariable Long id) {
        Faculty faculty = studentService.getFacultyOfStudent(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }
    @GetMapping(value = "/print-parallel")
    public String printStudentsParallel() {
        return studentService.printStudentsParallel();
    }

    @GetMapping(value = "/print-synchronized")
    public String printStudentSynchronizedParallel() {
        return studentService.printStudentsSynchronizedParallel();
    }
}
