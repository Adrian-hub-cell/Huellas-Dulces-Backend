package com.huellasdulces.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.huellasdulces.model.Customer;
import com.huellasdulces.repository.CustomerRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder encoder,
            CustomerRepository customerRepository) {

        InMemoryUserDetailsManager defaultUsers =
                new InMemoryUserDetailsManager(
                        User.withUsername("admin")
                                .password(encoder.encode("AdminDev2026!"))
                                .roles("ADMIN")
                                .build(),

                        User.withUsername("usuario")
                                .password(encoder.encode("UsuarioDev2026!"))
                                .roles("USER")
                                .build()
                );

        return username -> {
            try {
                return defaultUsers.loadUserByUsername(username);
            } catch (UsernameNotFoundException ignored) {
                Customer customer = customerRepository
                        .findByUsernameIgnoreCase(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Usuario no encontrado"
                                )
                        );

                return User.withUsername(customer.getUsername())
                        .password(customer.getPasswordHash())
                        .roles("USER")
                        .build();
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<String> roles = jwt.getClaimAsStringList("roles");

            Collection<GrantedAuthority> authorities = new ArrayList<>();

            if (roles != null) {
                for (String role : roles) {
                    if (role != null && role.startsWith("ROLE_")) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                }
            }

            return authorities;
        });

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .cors(Customizer.withDefaults())

            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/auth/register",
                        "/api/v1/auth/login"
                ).permitAll()

                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()

                .requestMatchers("/ws").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**")
                    .hasAnyRole("USER", "ADMIN")

                .requestMatchers("/api/v1/products/**")
                    .hasRole("ADMIN")

                .requestMatchers("/ws").permitAll()
            .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(
                            jwtAuthenticationConverter()
                    )
                )
            );

        return http.build();
    }
}

