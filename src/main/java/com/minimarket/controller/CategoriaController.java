package com.minimarket.controller;

import com.minimarket.entity.Categoria;
import com.minimarket.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@Tag(name = "Categoria", description = "Gestión de categorías de productos del Minimarket Plus")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Operation(summary = "Listar todas las categorías", description = "Devuelve todas las categorías registradas, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class)))
    })
    @GetMapping
    public CollectionModel<EntityModel<Categoria>> listarCategorias() {
        List<EntityModel<Categoria>> categorias = categoriaService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(categorias,
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withSelfRel());
    }

    @Operation(summary = "Obtener una categoría por ID", description = "Busca una categoría según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Categoria>> obtenerCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toModel(categoria));
    }

    @Operation(summary = "Crear una nueva categoría", description = "Registra una nueva categoría de productos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría creada correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class)))
    })
    @PostMapping
    public EntityModel<Categoria> guardarCategoria(@RequestBody Categoria categoria) {
        Categoria guardada = categoriaService.save(categoria);
        return toModel(guardada);
    }

    @Operation(summary = "Actualizar una categoría existente", description = "Actualiza los datos de una categoría ya registrada según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Categoria>> actualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        Categoria categoriaExistente = categoriaService.findById(id);
        if (categoriaExistente != null) {
            categoria.setId(id);
            Categoria actualizada = categoriaService.save(categoria);
            return ResponseEntity.ok(toModel(actualizada));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una categoría", description = "Elimina una categoría del sistema según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            categoriaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Método auxiliar: arma el EntityModel con los enlaces HATEOAS de cada categoría
    private EntityModel<Categoria> toModel(Categoria categoria) {
        return EntityModel.of(categoria,
                linkTo(methodOn(CategoriaController.class).obtenerCategoriaPorId(categoria.getId())).withSelfRel(),
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withRel("categorias"));
    }
}