package org.example.security.controller;

import org.example.security.domain.Student;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author renc
 */
@RequestMapping("/api/v1/student")
@RestController
public class StudentController {

    private static final List<Student> students = Arrays.asList(
            new Student("1", "stu"),
            new Student("2", "trainee"),
            new Student("3", "admin")
    );

    @GetMapping("/{id}")
    public Student findById(@PathVariable String id) {
        return students.stream()
                .filter(stu -> id.equals(stu.getId()))
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_TRAINEE')")
    public List<Student> findAll() {
        return students;
    }

    @PostMapping
    // @PreAuthorize("hasAuthority('student:write')")
    public void insertStudent(Student student) {
        System.out.println("insert: " + student);
    }

    @PutMapping("{id}")
    // @PreAuthorize("hasAuthority('student:write')")
    public void updateStudent(@PathVariable String id, Student student) {
        System.out.println("update: " + id + " with " + student);
    }

    @DeleteMapping("{id}")
    // @PreAuthorize("hasAuthority('student:write')")
    public void deleteStudent(@PathVariable String id) {
        System.out.println("delete: " + id);
    }
}
