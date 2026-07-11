package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
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
@RequestMapping("/api/productos")
@Tag(name = "Producto", description = "Gestión de productos del Minimarket Plus")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Listar todos los productos", description = "Devuelve la lista completa de productos registrados, cada uno con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class)))
    })
    @GetMapping
    public CollectionModel<EntityModel<Producto>> listarProductos() {
        List<EntityModel<Producto>> productos = productoService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(productos,
                linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel());
    }

    @Operation(summary = "Obtener un producto por ID", description = "Busca un producto según su identificador y retorna sus datos junto con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerProductoPorId(@PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toModel(producto));
    }

    @Operation(summary = "Crear un nuevo producto", description = "Registra un nuevo producto en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto creado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class)))
    })
    @PostMapping
    public EntityModel<Producto> guardarProducto(@RequestBody Producto producto) {
        Producto guardado = productoService.save(producto);
        return toModel(guardado);
    }

    @Operation(summary = "Actualizar un producto existente", description = "Actualiza los datos de un producto ya registrado según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        Producto productoExistente = productoService.findById(id);
        if (productoExistente != null) {
            producto.setId(id);
            Producto actualizado = productoService.save(producto);
            return ResponseEntity.ok(toModel(actualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un producto", description = "Elimina un producto del sistema según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Método auxiliar: arma el EntityModel con los enlaces HATEOAS de cada producto
    private EntityModel<Producto> toModel(Producto producto) {
        return EntityModel.of(producto,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
    }
}