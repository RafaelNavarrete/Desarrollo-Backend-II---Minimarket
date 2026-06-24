package com.minimarket;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventarioTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Inventario inventario;
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

        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(10);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());
    }

    // --- Pruebas de entidad Inventario ---

    @Test
    void crearMovimientoInventario_DatosValidos_AtributosCorrectos() {
        assertNotNull(inventario);
        assertNotNull(inventario.getProducto());
        assertEquals(10, inventario.getCantidad());
        assertEquals("Entrada", inventario.getTipoMovimiento());
        assertNotNull(inventario.getFechaMovimiento());
    }

    @Test
    void inventario_TieneProductoAsociado_RetornaTrue() {
        assertNotNull(inventario.getProducto());
        assertEquals("Leche", inventario.getProducto().getNombre());
    }

    @Test
    void inventario_CantidadPositiva_RetornaTrue() {
        assertTrue(inventario.getCantidad() > 0);
    }

    @Test
    void inventario_TipoMovimientoValido_RetornaTrue() {
        String tipo = inventario.getTipoMovimiento();
        assertTrue(tipo.equals("Entrada") || tipo.equals("Salida"));
    }

    @Test
    void inventario_FechaMovimientoNoNula_RetornaTrue() {
        assertNotNull(inventario.getFechaMovimiento());
    }

    @Test
    void inventario_ActualizarCantidad_DatosCorrectos() {
        inventario.setCantidad(25);
        assertEquals(25, inventario.getCantidad());
    }

    @Test
    void inventario_ActualizarTipoMovimiento_DatosCorrectos() {
        inventario.setTipoMovimiento("Salida");
        assertEquals("Salida", inventario.getTipoMovimiento());
    }

    // --- Pruebas de Service con Mockito ---

    @Test
    void findAll_MovimientosExistentes_RetornaLista() {
        List<Inventario> movimientos = Arrays.asList(inventario);
        when(inventarioRepository.findAll()).thenReturn(movimientos);

        List<Inventario> resultado = inventarioService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Entrada", resultado.get(0).getTipoMovimiento());
        verify(inventarioRepository, times(1)).findAll();
    }

    @Test
    void findById_MovimientoExistente_RetornaInventario() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        Inventario resultado = inventarioService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10, resultado.getCantidad());
        assertEquals("Entrada", resultado.getTipoMovimiento());
        verify(inventarioRepository, times(1)).findById(1L);
    }

    @Test
    void findById_MovimientoNoExistente_RetornaNull() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        Inventario resultado = inventarioService.findById(99L);

        assertNull(resultado);
        verify(inventarioRepository, times(1)).findById(99L);
    }

    @Test
    void save_MovimientoValido_RetornaMovimientoGuardado() {
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario resultado = inventarioService.save(inventario);

        assertNotNull(resultado);
        assertEquals(10, resultado.getCantidad());
        assertEquals("Entrada", resultado.getTipoMovimiento());
        assertNotNull(resultado.getProducto());
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    void deleteById_MovimientoExistente_EliminaCorrectamente() {
        doNothing().when(inventarioRepository).deleteById(1L);

        inventarioService.deleteById(1L);

        verify(inventarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByProductoId_ProductoConMovimientos_RetornaLista() {
        List<Inventario> movimientos = Arrays.asList(inventario);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(movimientos);

        List<Inventario> resultado = inventarioService.findByProductoId(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Entrada", resultado.get(0).getTipoMovimiento());
        verify(inventarioRepository, times(1)).findByProductoId(1L);
    }

    @Test
    void findByProductoId_ProductoSinMovimientos_RetornaListaVacia() {
        when(inventarioRepository.findByProductoId(99L)).thenReturn(List.of());

        List<Inventario> resultado = inventarioService.findByProductoId(99L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(inventarioRepository, times(1)).findByProductoId(99L);
    }
    
}