package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Gestión de movimientos de inventario del Minimarket Plus")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Listar movimientos de inventario", description = "Devuelve todos los movimientos de entrada y salida registrados, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inventario.class)))
    })
    @GetMapping
    public CollectionModel<EntityModel<Inventario>> listarMovimientosDeInventario() {
        List<EntityModel<Inventario>> movimientos = inventarioService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(movimientos,
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario()).withSelfRel());
    }
    
    @Operation(summary = "Obtener un movimiento de inventario por ID", description = "Busca un movimiento de inventario según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toModel(inventario));
    }

    @Operation(summary = "Registrar un movimiento de inventario", description = "Crea un nuevo movimiento de entrada o salida de stock para un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento registrado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "400", description = "No se indicó un producto válido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> registrarMovimiento(@RequestBody Inventario inventario) {
        if (inventario.getProducto() == null || inventario.getProducto().getId() == null) {
            return ResponseEntity.badRequest().body("Debe indicar un producto válido para registrar el movimiento.");
        }
        Inventario guardado = inventarioService.save(inventario);
        return ResponseEntity.ok(toModel(guardado));
    }

    @Operation(summary = "Actualizar un movimiento de inventario", description = "Actualiza los datos de un movimiento existente según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> actualizarMovimiento(@PathVariable Long id, @RequestBody Inventario inventario) {
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            inventario.setId(id);
            Inventario actualizado = inventarioService.save(inventario);
            return ResponseEntity.ok(toModel(actualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un movimiento de inventario", description = "Elimina un movimiento de inventario según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Método auxiliar: arma el EntityModel con los enlaces HATEOAS de cada movimiento
    private EntityModel<Inventario> toModel(Inventario inventario) {
        return EntityModel.of(inventario,
                linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(inventario.getId())).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario()).withRel("inventario"),
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(inventario.getProducto().getId())).withRel("producto"));
    }
}