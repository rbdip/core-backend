package ru.stepagin.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.stepagin.backend.entity.UserEntity;
import ru.stepagin.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException(username);
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
//                .authorities(new ArrayList<>())
                .build();
    }
}
