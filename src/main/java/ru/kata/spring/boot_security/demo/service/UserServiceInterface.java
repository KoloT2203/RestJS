package ru.kata.spring.boot_security.demo.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.security.Principal;
import java.util.List;

public interface UserServiceInterface {
    void createUser(User user);
    List<User> getAllUsers();
    User getUserById(Integer id);
    ResponseEntity<?> deleteUserById(Integer id, Principal principal);
    void updateUser(Integer id, User user);
    UserDetails loadUserByUsername(String username);
    List<Role> getRoles();
    ResponseEntity<?> saveUser(Integer id, User user, Principal principal);
}
