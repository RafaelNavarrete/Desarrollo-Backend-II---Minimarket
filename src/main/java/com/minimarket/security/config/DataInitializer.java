package com.minimarket.security.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        Rol rolAdministrador = rolRepository.findByNombre("ROLE_ADMINISTRADOR")
                .orElseGet(() -> rolRepository.save(crearRol("ROLE_ADMINISTRADOR")));

        Rol rolEmpleado = rolRepository.findByNombre("ROLE_EMPLEADO")
                .orElseGet(() -> rolRepository.save(crearRol("ROLE_EMPLEADO")));

        Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE")
                .orElseGet(() -> rolRepository.save(crearRol("ROLE_CLIENTE")));

        if (usuarioRepository.findByUsername("administrador").isEmpty()) {
            Usuario administrador = new Usuario();
            administrador.setUsername("administrador");
            administrador.setPassword(passwordEncoder.encode("administrador123"));
            administrador.setRoles(Set.of(rolAdministrador));
            usuarioRepository.save(administrador);
        }

        if (usuarioRepository.findByUsername("empleado").isEmpty()) {
            Usuario empleado = new Usuario();
            empleado.setUsername("empleado");
            empleado.setPassword(passwordEncoder.encode("empleado123"));
            empleado.setRoles(Set.of(rolEmpleado));
            usuarioRepository.save(empleado);
        }

        if (usuarioRepository.findByUsername("cliente").isEmpty()) {
            Usuario cliente = new Usuario();
            cliente.setUsername("cliente");
            cliente.setPassword(passwordEncoder.encode("cliente123"));
            cliente.setRoles(Set.of(rolCliente));
            usuarioRepository.save(cliente);
        }

        System.out.println(">>> Datos iniciales cargados correctamente.");
    }

    private Rol crearRol(String nombre) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        return rol;
    }
}