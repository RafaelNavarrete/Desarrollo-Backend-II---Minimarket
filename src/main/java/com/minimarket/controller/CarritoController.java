package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
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
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Gestión del carrito de compras de MiniMarket Plus")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

     @Operation(summary = "Listar todos los items del carrito", description = "Devuelve todos los registros del carrito de todos los usuarios, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Carrito.class)))
    })
    @GetMapping
    public CollectionModel<EntityModel<Carrito>> listarCarrito() {
        List<EntityModel<Carrito>> items = carritoService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(items,
                linkTo(methodOn(CarritoController.class).listarCarrito()).withSelfRel());
    }

     @Operation(summary = "Obtener un item del carrito por ID", description = "Busca un registro del carrito según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "404", description = "Item no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Carrito>> obtenerCarritoPorId(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toModel(carrito));
    }

    @Operation(summary = "Agregar un producto al carrito", description = "Crea un nuevo registro en el carrito asociando un producto y una cantidad a un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado correctamente al carrito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "400", description = "No se indicó un producto válido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> agregarProductoAlCarrito(@RequestBody Carrito carrito) {
        if (carrito.getProducto() == null || carrito.getProducto().getId() == null) {
            return ResponseEntity.badRequest().body("Debe indicar un producto válido para agregar al carrito.");
        }
        Carrito guardado = carritoService.save(carrito);
        return ResponseEntity.ok(toModel(guardado));
    }

    @Operation(summary = "Actualizar un item del carrito", description = "Actualiza la cantidad o los datos de un item existente en el carrito.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "404", description = "Item no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Carrito>> actualizarCarrito(@PathVariable Long id, @RequestBody Carrito carrito) {
        Carrito existente = carritoService.findById(id);
        if (existente != null) {
            carrito.setId(id);
            Carrito actualizado = carritoService.save(carrito);
            return ResponseEntity.ok(toModel(actualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un producto del carrito", description = "Elimina un registro del carrito según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado del carrito correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Item no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Método auxiliar para convertir un Carrito en un EntityModel con enlaces HATEOAS
    private EntityModel<Carrito> toModel(Carrito carrito) {
        return EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.getId())).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carrito"),
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(carrito.getProducto().getId())).withRel("producto"));
    }
}
