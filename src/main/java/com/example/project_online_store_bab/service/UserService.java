package com.example.project_online_store_bab.service;

import com.example.project_online_store_bab.model.User;
import com.example.project_online_store_bab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        logger.info("Пользователь зарегистрирован: {}", user.getUsername());
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", id);
                    return new IllegalArgumentException("User not found");
                });
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Пользователь с именем {} не найден", username);
                    return new IllegalArgumentException("User not found");
                });
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }


    public void saveUser(User user) {
        userRepository.save(user);
        logger.info("Данные пользователя сохранены: {}", user.getUsername());
    }

    public void updateUser(Long id, User updatedUser) {
        User existingUser = findById(id);

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhone() != null && !updatedUser.getPhone().isEmpty()) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getPhoto() != null && !updatedUser.getPhoto().isEmpty()) {
            existingUser.setPhoto(updatedUser.getPhoto());
        }

        userRepository.save(existingUser);
        logger.info("Данные пользователя с ID {} обновлены", id);
    }
}