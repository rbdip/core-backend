package ru.stepagin.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.stepagin.backend.dto.CreateAccountDtoRequest;
import ru.stepagin.backend.dto.UpdateUserDtoRequest;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.exception.UserNotFoundException;
import ru.stepagin.backend.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectService projectService;

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity create(
            @Valid CreateAccountDtoRequest request
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
        UserEntity userToDelete = userRepository.findByUsername(userToDeleteName);
        if (userToDelete == null) {
            throw new UserNotFoundException(userToDeleteName);
        }
        projectService.deleteAllProjectsByUser(userToDelete);
        userRepository.deleteByUsername(userToDeleteName);
    }

    public void login(UserEntity user) {
        user.setLastLoginOn(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public UserEntity updateUserData(
            String userToUpdate,
            @Valid UpdateUserDtoRequest request
    ) {
        UserEntity user = getByUsername(userToUpdate);
        if (request.getDisplayName() == null
                && request.getPassword() == null) {
            throw new IllegalArgumentException("At least one parameter is required");
        }

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setUpdatedOn(LocalDateTime.now());
        return userRepository.save(user);
    }
}
