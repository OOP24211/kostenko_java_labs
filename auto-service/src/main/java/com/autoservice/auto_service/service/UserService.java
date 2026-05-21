package com.autoservice.auto_service.service;

import com.autoservice.auto_service.model.User;
import com.autoservice.auto_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Регистрация нового пользователя
    public User registerUser(String username, String email, String rawPassword,
                             String fullName, String phone) {
        // Проверяем, не занят ли username
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Имя пользователя уже занято");
        }

        // Проверяем, не занят ли email
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email уже используется");
        }

        // Хэшируем пароль
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Создаём пользователя
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setFullName(fullName);
        user.setPhone(phone);

        // Сохраняем в базу
        return userRepository.save(user);
    }

    // Поиск пользователя по имени (для входа)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Проверка пароля при входе
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Сохранение пользователя
    public User save(User user) {
        return userRepository.save(user);
    }
}