package com.minimarket.controller;

import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuario", description = "Gestión de usuarios del Minimarket Plus")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Listar todos los usuarios", description = "Devuelve todos los usuarios registrados (sin exponer contraseñas), con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    })
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> listarUsuarios() {
        List<EntityModel<Usuario>> usuarios = usuarioService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel());
    }

    @Operation(summary = "Obtener un usuario por ID", description = "Busca un usuario según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(u -> ResponseEntity.ok(toModel(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Registra un nuevo usuario, encriptando su contraseña antes de guardarla.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    })
    @PostMapping
    public EntityModel<Usuario> guardarUsuario(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario guardado = usuarioService.save(usuario);
        return toModel(guardado);
    }

    @Operation(summary = "Actualizar un usuario existente", description = "Actualiza los datos de un usuario ya registrado, re-encriptando su contraseña.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            usuario.setId(id);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            Usuario actualizado = usuarioService.save(usuario);
            return ResponseEntity.ok(toModel(actualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario del sistema según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Método auxiliar: arma el EntityModel con los enlaces HATEOAS, ocultando el password en la respuesta
    private EntityModel<Usuario> toModel(Usuario usuario) {
        usuario.setPassword(null); // nunca exponer el hash de la contraseña en la respuesta
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios"));
    }
}