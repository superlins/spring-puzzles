package org.example.keycloak.controller;

import org.example.keycloak.domain.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author renc
 */
@RestController
public class CustomerController {

    @GetMapping("/customer")
    public ResponseEntity<Customer> findAll() {
        return ResponseEntity.ok(new Customer(1L, "demo", "bj"));
    }
}
