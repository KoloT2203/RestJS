package ru.kata.spring.boot_security.demo.Dao;

import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.security.Principal;
import java.util.List;

public interface UserDaoInterface {
    List<User> index();
    User getUserById(Integer id);
    void save(User user);
    void update(Integer id, User updateUser);
    void deleteById(Integer id, Principal principal);
    User findByUsername(String username);
    List<Role> getRoles();
    Role findRoleByName(String name);
    User saveUser(User user);
}

