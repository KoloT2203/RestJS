package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class MainRestController {

    private final UserServiceInterface userService;

    @Autowired
    public MainRestController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardData> getDashboardData(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        DashboardData data = new DashboardData();

        data.setCurrentUser(currentUser);
        data.setUsers(userService.getAllUsers());
        data.setRoles(userService.getRoles());

        return ResponseEntity.ok(data);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            request.logout(); // Выполняем выход
            return ResponseEntity.ok("Logged out successfully");
        } catch (ServletException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Logout failed");
        }
    }
}