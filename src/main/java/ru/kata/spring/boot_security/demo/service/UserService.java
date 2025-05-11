package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.Dao.UserDaoInterface;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserService implements UserServiceInterface, UserDetailsService {

    private final UserDaoInterface userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public UserService(UserDaoInterface userDao, PasswordEncoder passwordEncoder){
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createUser(User user) {

        Set<Role> managedRoles = new HashSet<>();
        for (Role role : user.getRoles()) {
            Role existingRole = userDao.findRoleByName(role.getName());
            managedRoles.add(Objects.requireNonNullElse(existingRole, role));
        }
        user.setRoles(managedRoles);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userDao.save(user);
    }

    @Override
    @Transactional
    public List<Role> getRoles(){
        return userDao.getRoles();
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userDao.index();
    }

    @Override
    @Transactional
    public User getUserById(Integer id) {
        return userDao.getUserById(id);
    }

    @Override
    @Transactional
    public void deleteUserById(Integer id, Principal principal) {
        userDao.deleteById(id, principal);
    }

    @Override
    @Transactional
    public void updateUser(Integer id, User user) {
       userDao.update(id, user);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userDao.saveUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
