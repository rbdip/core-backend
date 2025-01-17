package ru.stepagin.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.stepagin.backend.dto.UserCardDtoResponse;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.mapper.UserMapper;
import ru.stepagin.backend.service.UserService;

@Slf4j
@RestController
@RequestMapping("${app.path.start-prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserCardDtoResponse> getUserCard(
            @PathVariable(name = "username") String username
    ) {
        UserEntity user = userService.getByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserMapper.toCard(user));
    }


}
