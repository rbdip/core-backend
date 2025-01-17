package ru.stepagin.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.stepagin.backend.dto.CreateAccountDtoRequest;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity create(CreateAccountDtoRequest request) {
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        user.setDisplayName(request.getDisplayName());
        return userRepository.save(user);
    }
}
