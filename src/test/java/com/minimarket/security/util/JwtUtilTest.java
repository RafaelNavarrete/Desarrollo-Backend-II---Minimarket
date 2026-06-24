package com.minimarket.security.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "M1n1m4rk3t_S3cr3t_K3y_D3s4rr0ll0_2026!");
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", 86400000L);
    }


    // --- generateToken / extractUsername / extractRole ---
    @Test
    void generateToken_UsuarioValido_RetornaTokenNulo() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMINISTRADOR");
        assertNotNull(token);
    }

    @Test
    void extractRole_TokenValido_RetornaRoleCorrecto() {
        String token = jwtUtil.generateToken("cajero1", "ROLE_CLIENTE");
        assertEquals("ROLE_CLIENTE", jwtUtil.extractRole(token));
    }

    @Test
    void extractRole_TokenValido_RetornaRolCorrecto() {
        String token = jwtUtil.generateToken("cliente1", "ROLE_CLIENTE");
        assertEquals("ROLE_CLIENTE", jwtUtil.extractRole(token));
    }

    // --- validarToken ---
    @Test
    void validateToken_TokenValido_RetornaTrue() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMINISTRADOR");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_TokenInvalido_RetornaFalse() {
        assertFalse(jwtUtil.validateToken("esto.no.es.un.token.valido"));
    }

    @Test
    void validateToken_TokenNulo_RetornaFalse() {
        assertFalse(jwtUtil.validateToken(""));
    }

    // --- expiración ---

    @Test
    void validateToken_TokenExpirado_RetornaFalse() {
        // seteamos expiración en -1ms para que ya esté vencido al generarlo
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", -1L);
        String token = jwtUtil.generateToken("admin", "ROLE_ADMINISTRADOR");
        assertFalse(jwtUtil.validateToken(token));
    }
}  