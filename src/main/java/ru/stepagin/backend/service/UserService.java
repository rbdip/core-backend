package ru.stepagin.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
