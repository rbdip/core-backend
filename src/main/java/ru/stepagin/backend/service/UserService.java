package ru.stepagin.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.stepagin.backend.dto.CreateAccountDtoRequest;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity create(
            @Validated CreateAccountDtoRequest request
    ) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        user.setDisplayName(request.getDisplayName());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String userToDeleteName) {
        userRepository.deleteByUsername(userToDeleteName);
    }

    public void login(UserEntity user) {
        user.setLastLoginOn(LocalDateTime.now());
        userRepository.save(user);
    }
}
