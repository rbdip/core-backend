package ru.stepagin.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.backend.dto.UpdateUserDtoRequest;
import ru.stepagin.backend.dto.UserCardDtoResponse;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.mapper.UserMapper;
import ru.stepagin.backend.service.UserService;

import java.security.Principal;

@Slf4j
@CrossOrigin("*")
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

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable(name = "username") String userToDelete,
            Principal principal
    ) {
        String actorName = principal.getName();
        if (!actorName.equals(userToDelete)) {
            throw new IllegalArgumentException("cannot delete other users");
        }
        userService.deleteUser(userToDelete);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{username}")
    public ResponseEntity<UserCardDtoResponse> updateUser(
            @PathVariable(name = "username") String userToUpdate,
            @RequestBody UpdateUserDtoRequest request,
            Principal principal
    ) {
        String actorName = principal.getName();
        if (!actorName.equals(userToUpdate)) {
            throw new IllegalArgumentException("cannot update other users");
        }
        UserEntity updated = userService.updateUserData(userToUpdate, request);
        return ResponseEntity.ok(UserMapper.toCard(updated));
    }
}
