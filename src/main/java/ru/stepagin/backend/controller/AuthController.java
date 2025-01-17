package ru.stepagin.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.stepagin.backend.dto.CreateAccountDtoRequest;
import ru.stepagin.backend.dto.UserCardDtoResponse;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.mapper.UserMapper;
import ru.stepagin.backend.service.UserService;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("${app.path.start-prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserCardDtoResponse> register(
            @RequestBody CreateAccountDtoRequest request
    ) {
        if (userService.getByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("Username is already in use");
        }
        UserEntity newUser = userService.create(request);
        return ResponseEntity.ok(UserMapper.toCard(newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<UserCardDtoResponse> login(Principal principal) {
        String username = principal.getName();
        UserEntity user = userService.getByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.login(user);
        return ResponseEntity.ok(UserMapper.toCard(user));
    }
}
