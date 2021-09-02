package org.example.security.controller;

import org.example.security.domain.Student;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author renc
 */
@RestController
@RequestMapping("/management/api/v1/student")
public class StudentManagerController {

    private static final List<Student> students = Arrays.asList(
            new Student("1", "stu"),
            new Student("2", "trainee"),
            new Student("3", "admin")
    );

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
