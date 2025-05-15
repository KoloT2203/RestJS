package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    // Вспомогательный класс для структурирования данных
    private static class DashboardData {
        private User currentUser;
        private List<User> users;
        private List<Role> roles;

        // Геттеры и сеттеры
        public User getCurrentUser() {
            return currentUser;
        }

        public void setCurrentUser(User currentUser) {
            this.currentUser = currentUser;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        public List<Role> getRoles() {
            return roles;
        }

        public void setRoles(List<Role> roles) {
            this.roles = roles;
        }
    }
}