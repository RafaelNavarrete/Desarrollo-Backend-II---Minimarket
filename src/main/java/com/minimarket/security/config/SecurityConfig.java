package com.minimarket.security.config;

import com.minimarket.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF con la nueva sintaxis
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll() // Permitir acceso público
                        .requestMatchers("/h2-console/**").permitAll() // Permitir acceso a la consola H2
                        .requestMatchers("/api/usuarios/**").hasRole("GERENTE") // Solo GERENTE puede acceder a /api/usuarios
                        .requestMatchers("/api/inventario/**").hasAnyRole("GERENTE", "EMPLEADO") // GERENTE y EMPLEADO pueden acceder a /api/inventario
                        .requestMatchers("/api/ventas/**", "/api/detalleVenta/**").hasAnyRole("GERENTE", "EMPLEADO") // GERENTE y EMPLEADO pueden acceder a /api/ventas y /api/detalleVenta
                        .requestMatchers("/api/productos/**", "/api/categorias/**").hasAnyRole("GERENTE", "EMPLEADO", "CLIENTE") // GERENTE, EMPLEADO y CLIENTE pueden acceder a /api/productos y /api/categorias
                        .requestMatchers("/api/carrito/**").hasAnyRole("GERENTE", "EMPLEADO", "CLIENTE") // GERENTE, EMPLEADO y CLIENTE pueden acceder a /api/carrito
                        .anyRequest().authenticated() // Requiere autenticación para el resto
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/public/hola", true) // Redirigir después del login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/public/hola")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Configuración de encriptación de contraseñas
    }
}
