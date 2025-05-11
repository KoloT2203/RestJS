package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/login")
    public String loginPage() {
        return "redirect:dashboard";
    }

    @GetMapping("/login?logout")
    public String showLogoutPage() {
        return "login"; // Вернуть страницу входа с сообщением о выходе
    }
}