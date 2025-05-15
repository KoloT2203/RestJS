package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserServiceInterface;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminRestController {

    private final UserServiceInterface userService;

    @Autowired
    public AdminRestController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(null, user, principal));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @RequestBody User updatedUser,
            Principal principal) {
        return userService.saveUser(id, updatedUser, principal);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Integer id,
            Principal principal) {
        return userService.deleteUserById(id, principal);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(userService.getRoles());
    }
}
