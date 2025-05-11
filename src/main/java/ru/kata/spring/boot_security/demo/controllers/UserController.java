package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.service.UserServiceInterface;

import java.security.Principal;

@Controller
public class UserController {

    private final UserServiceInterface userService;

    @Autowired
    public UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String showUserInfo(Principal principal, Model model) {
        UserDetails user = userService.loadUserByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("content", "user :: content");
        return "dashboard"; // Шаблон для отображения информации
    }

    @GetMapping("/dashboard")
    public String showDashboard(Principal principal, Model model, Authentication auth) {
        UserDetails user = userService.loadUserByUsername(principal.getName());
        model.addAttribute("currentUser", userService.loadUserByUsername(auth.getName()));
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", userService.getRoles());
        model.addAttribute("content", "dashboard :: content");
        return "dashboard";
    }
}
