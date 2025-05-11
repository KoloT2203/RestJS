package ru.kata.spring.boot_security.demo.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.Role;
import  ru.kata.spring.boot_security.demo.models.User;

import javax.persistence.*;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserDao implements UserDaoInterface {

    @PersistenceContext
    private EntityManager em;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDao(EntityManager em, PasswordEncoder passwordEncoder) {
        this.em = em;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Role> getRoles() {
        return em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
    }

    @Override
    public List<User> index() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public User getUserById(Integer id) {
        return em.find(User.class, id);
    }

    @Override
    public void save(User user) {
        em.persist(user);
    }

    @Override
    public void update(Integer id, User updateUser) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String check = "";
        if (index().stream()
                .filter(x -> x.getUsername().equals(updateUser.getUsername()))
                .findAny()
                .orElse(null) != null && !updateUser.getUsername().equals(getUserById(id).getUsername())){
            return;
        }
        User user = em.find(User.class, id);
        if (user.getUsername().equals(userDetails.getUsername())) {
            check = "admin";
        }
        user.setUsername(updateUser.getUsername());

        String pass = updateUser.getPassword();
        user.setPassword(passwordEncoder.encode(pass));

        Set<Role> managedRoles = new HashSet<>();
        for (Role role : updateUser.getRoles()) {
            Role persistentRole = findRoleByName(role.getName());
            if (persistentRole == null) {
                em.persist(role);
            }
            managedRoles.add(persistentRole);
        }
        user.getRoles().clear();
        user.getRoles().addAll(managedRoles);
        user.setAge(updateUser.getAge());
        user.setName(updateUser.getName());
        user.setLastName(updateUser.getLastName());
        em.merge(user);
        if (check.equals("admin")) {
            UserDetails updatedUser = findByUsername(updateUser.getUsername());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUser,
                    updatedUser.getPassword(),
                    updatedUser.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    @Override
    public void deleteById(Integer id, Principal principal) {
        User user = em.find(User.class, id);
        if (user != null) {
            if (principal != null && principal.getName().equals(user.getUsername())) {
                em.remove(user);
                SecurityContextHolder.clearContext();
            } else {
                em.remove(user);
            }
        }
    }

    @Override
    public User saveUser(User user) {
        // Шифруем пароль, если он был изменен
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Обрабатываем роли
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> managedRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                // Для существующих ролей используем merge
                if (role.getId() != null) {
                    Role managedRole = em.find(Role.class, role.getId());
                    if (managedRole == null) {
                        throw new IllegalArgumentException("Role with id " + role.getId() + " not found");
                    }
                    managedRoles.add(managedRole);
                } else {
                    em.persist(role);
                    managedRoles.add(role);
                }
            }
            user.setRoles(managedRoles);
        }

        if (user.getId() == null) {
            em.persist(user);
        } else {
            user = em.merge(user);
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return em.createQuery(
                        "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    @Override
    public Role findRoleByName(String name) {
        try {
            return em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
