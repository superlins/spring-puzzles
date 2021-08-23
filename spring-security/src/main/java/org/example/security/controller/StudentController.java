package org.example.security.controller;

import org.example.security.domain.Student;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
