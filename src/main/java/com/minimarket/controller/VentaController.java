package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
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
@RequestMapping("/api/ventas")
@Tag(name = "Venta", description = "Gestión de ventas del Minimarket Plus")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Operation(summary = "Listar todas las ventas", description = "Devuelve todas las ventas registradas, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class)))
    })
    @GetMapping
    public CollectionModel<EntityModel<Venta>> listarVentas() {
        List<EntityModel<Venta>> ventas = ventaService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(ventas,
                linkTo(methodOn(VentaController.class).listarVentas()).withSelfRel());
    }

    @Operation(summary = "Obtener una venta por ID", description = "Busca una venta según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        if (venta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toModel(venta));
    }

    @Operation(summary = "Registrar una nueva venta", description = "Crea una nueva venta asociada a un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta registrada correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class)))
    })
    @PostMapping
    public EntityModel<Venta> guardarVenta(@RequestBody Venta venta) {
        Venta guardada = ventaService.save(venta);
        return toModel(guardada);
    }

    // Método auxiliar: arma el EntityModel con los enlaces HATEOAS de cada venta
    private EntityModel<Venta> toModel(Venta venta) {
        return EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(venta.getId())).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"));
    }
}