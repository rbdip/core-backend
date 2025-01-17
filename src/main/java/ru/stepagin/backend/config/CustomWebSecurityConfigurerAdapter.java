package ru.stepagin.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomWebSecurityConfigurerAdapter {

    @Value("${app.path.start-prefix}")
    private String prefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                                .requestMatchers(HttpMethod.POST, prefix + "/auth/register").permitAll()
                                .requestMatchers(HttpMethod.GET, prefix + "/projects/").permitAll()
                                .requestMatchers(HttpMethod.GET, prefix + "/projects/*/*").permitAll()
                                .requestMatchers(HttpMethod.GET, prefix + "/users/*").permitAll()
                        .anyRequest().authenticated()
//                        .anyRequest().permitAll()
                )
                .formLogin(f -> f
//                        .loginPage(prefix + "/auth/login")
//                        .loginPage("/login")
//                        .permitAll()
                                .loginProcessingUrl("/api/v1/auth/login")
//                        .failureForwardUrl("/login")
                )
                .logout(l -> l
//                        .logoutSuccessUrl("/")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                        .logoutUrl("/api/v1/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))

                .rememberMe(Customizer.withDefaults())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))

                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
