package com.minimarket;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ProductoTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;  // Cambio aquí

    private Producto producto;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Lacteos");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setPrecio(1500.0);
        producto.setStock(50);
        producto.setCategoria(categoria);
    }

    // --- Pruebas de entidad Producto ---

    @Test
    void crearProducto_DatosValidos_AtributosCorrectos() {
        assertNotNull(producto);
        assertEquals("Leche", producto.getNombre());
        assertEquals(1500.0, producto.getPrecio());
        assertEquals(50, producto.getStock());
        assertNotNull(producto.getCategoria());
        assertEquals("Lacteos", producto.getCategoria().getNombre());
    }

    @Test
    void producto_PrecioPositivo_RetornaTrue() {
        assertTrue(producto.getPrecio() > 0);
    }

    @Test
    void producto_StockPositivo_RetornaTrue() {
        assertTrue(producto.getStock() > 0);
    }

    @Test
    void producto_TieneCategoriaAsignada_RetornaTrue() {
        assertNotNull(producto.getCategoria());
    }

    @Test
    void producto_ActualizarPrecio_DatosCorrectos() {
        producto.setPrecio(2000.0);
        assertEquals(2000.0, producto.getPrecio());
    }

    @Test
    void producto_ActualizarStock_DatosCorrectos() {
        producto.setStock(30);
        assertEquals(30, producto.getStock());
    }

    // --- Pruebas de Service con Mockito ---

    @Test
    void findAll_ProductosExistentes_RetornaLista() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> resultado = productoService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Leche", resultado.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void findById_ProductoExistente_RetornaProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.findById(1L);

        assertNotNull(resultado);
        assertEquals("Leche", resultado.getNombre());
        assertEquals(1500.0, resultado.getPrecio());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ProductoNoExistente_RetornaNull() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto resultado = productoService.findById(99L);

        assertNull(resultado);
        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    void save_ProductoValido_RetornaProductoGuardado() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.save(producto);

        assertNotNull(resultado);
        assertEquals("Leche", resultado.getNombre());
        assertEquals(1500.0, resultado.getPrecio());
        assertNotNull(resultado.getCategoria());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void deleteById_ProductoExistente_EliminaCorrectamente() {
        doNothing().when(productoRepository).deleteById(1L);

        productoService.deleteById(1L);

        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByCategoriaId_CategoriaExistente_RetornaListaProductos() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByCategoriaId(1L)).thenReturn(productos);

        List<Producto> resultado = productoService.findByCategoriaId(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Leche", resultado.get(0).getNombre());
        verify(productoRepository, times(1)).findByCategoriaId(1L);
    }

    @Test
    void findByCategoriaId_CategoriaSinProductos_RetornaListaVacia() {
        when(productoRepository.findByCategoriaId(99L)).thenReturn(List.of());

        List<Producto> resultado = productoService.findByCategoriaId(99L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(productoRepository, times(1)).findByCategoriaId(99L);
    }
}