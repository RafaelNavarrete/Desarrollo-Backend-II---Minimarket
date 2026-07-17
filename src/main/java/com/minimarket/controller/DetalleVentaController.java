package com.minimarket.controller;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.service.DetalleVentaService;
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
@RequestMapping("/api/detalle-ventas")
@Tag(name = "DetalleVenta", description = "Gestión del detalle de productos vendidos en cada venta del Minimarket Plus")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Operation(summary = "Listar todos los detalles de venta", description = "Devuelve todos los detalles de venta registrados, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleVenta.class)))
    })
    @GetMapping
    public CollectionModel<EntityModel<DetalleVenta>> listarDetalleVentas() {
        List<EntityModel<DetalleVenta>> detalles = detalleVentaService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(detalles,
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withSelfRel());
    }

    @Operation(summary = "Obtener un detalle de venta por ID", description = "Busca un detalle de venta según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de venta encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleVenta.class))),
            @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DetalleVenta>> obtenerDetalleVentaPorId(@PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toModel(detalleVenta));
    }

    @Operation(summary = "Crear un nuevo detalle de venta", description = "Registra un nuevo detalle asociando un producto y una cantidad a una venta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de venta creado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleVenta.class))),
            @ApiResponse(responseCode = "400", description = "Faltan datos de producto o venta", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> guardarDetalleVenta(@RequestBody DetalleVenta detalleVenta) {
        if (detalleVenta.getProducto() == null || detalleVenta.getProducto().getId() == null) {
            return ResponseEntity.badRequest().body("Debe indicar un producto válido para el detalle de venta.");
        }
        if (detalleVenta.getVenta() == null || detalleVenta.getVenta().getId() == null) {
            return ResponseEntity.badRequest().body("Debe indicar una venta válida para el detalle de venta.");
        }
        DetalleVenta guardado = detalleVentaService.save(detalleVenta);
        return ResponseEntity.ok(toModel(guardado));
    }

    @Operation(summary = "Actualizar un detalle de venta", description = "Actualiza los datos de un detalle de venta existente según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de venta actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleVenta.class))),
            @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<DetalleVenta>> actualizarDetalleVenta(@PathVariable Long id, @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente != null) {
            detalleVenta.setId(id);
            DetalleVenta actualizado = detalleVentaService.save(detalleVenta);
            return ResponseEntity.ok(toModel(actualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un detalle de venta", description = "Elimina un detalle de venta según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle de venta eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleVenta(@PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta != null) {
            detalleVentaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Método auxiliar: arma el EntityModel con los enlaces HATEOAS, incluyendo enlaces a la venta y al producto relacionados
    private EntityModel<DetalleVenta> toModel(DetalleVenta detalleVenta) {
        return EntityModel.of(detalleVenta,
                linkTo(methodOn(DetalleVentaController.class).obtenerDetalleVentaPorId(detalleVenta.getId())).withSelfRel(),
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withRel("detalle-ventas"),
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(detalleVenta.getVenta().getId())).withRel("venta"),
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(detalleVenta.getProducto().getId())).withRel("producto"));
    }
}