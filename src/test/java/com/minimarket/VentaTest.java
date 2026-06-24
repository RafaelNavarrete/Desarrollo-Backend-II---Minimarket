package com.minimarket;

import com.minimarket.entity.*;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VentaTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Venta venta;
    private Usuario usuario;
    private DetalleVenta detalle;
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

        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ROLE_CLIENTE");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente1");
        usuario.setPassword("cliente123");
        usuario.setRoles(Set.of(rol));

        detalle = new DetalleVenta();
        detalle.setId(1L);
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setPrecio(1500.0);  // Ahora usa setPrecio() correctamente

        venta = new Venta();
        venta.setId(1L);
        venta.setUsuario(usuario);
        venta.setFecha(new Date());
        venta.setDetalles(Arrays.asList(detalle));
    }

    // --- Pruebas de entidad Venta ---

    @Test
    void crearVenta_DatosValidos_AtributosCorrectos() {
        assertNotNull(venta);
        assertNotNull(venta.getUsuario());
        assertEquals("cliente1", venta.getUsuario().getUsername());
        assertNotNull(venta.getFecha());
        assertNotNull(venta.getDetalles());
        assertEquals(1, venta.getDetalles().size());
    }

    @Test
    void venta_TieneUsuarioAsociado_RetornaTrue() {
        assertNotNull(venta.getUsuario());
        assertEquals("cliente1", venta.getUsuario().getUsername());
    }

    @Test
    void venta_TieneDetalles_RetornaTrue() {
        assertNotNull(venta.getDetalles());
        assertFalse(venta.getDetalles().isEmpty());
    }

    @Test
    void venta_FechaNoNula_RetornaTrue() {
        assertNotNull(venta.getFecha());
    }

    @Test
    void venta_DetalleConPrecioCorrecto_RetornaTrue() {
        assertEquals(1500.0, detalle.getPrecio());
    }

    @Test
    void venta_DetalleCantidadPositiva_RetornaTrue() {
        assertTrue(detalle.getCantidad() > 0);
    }

    @Test
    void venta_CalcularTotal_RetornaSumaCorrecta() {
        double totalEsperado = venta.getDetalles().stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecio())
                .sum();
        assertEquals(3000.0, totalEsperado);
    }

    // --- Pruebas de Service con Mockito ---

    @Test
    void findAll_VentasExistentes_RetornaLista() {
        List<Venta> ventas = Arrays.asList(venta);
        when(ventaRepository.findAll()).thenReturn(ventas);

        List<Venta> resultado = ventaService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("cliente1", resultado.get(0).getUsuario().getUsername());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void findById_VentaExistente_RetornaVenta() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("cliente1", resultado.getUsuario().getUsername());
        assertNotNull(resultado.getDetalles());
        verify(ventaRepository, times(1)).findById(1L);
    }

    @Test
    void findById_VentaNoExistente_RetornaNull() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        Venta resultado = ventaService.findById(99L);

        assertNull(resultado);
        verify(ventaRepository, times(1)).findById(99L);
    }

    @Test
    void save_VentaValida_RetornaVentaGuardada() {
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta resultado = ventaService.save(venta);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertNotNull(resultado.getUsuario());
        assertNotNull(resultado.getDetalles());
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    void findByUsuarioId_UsuarioConVentas_RetornaLista() {
        List<Venta> ventas = Arrays.asList(venta);
        when(ventaRepository.findByUsuarioId(1L)).thenReturn(ventas);

        List<Venta> resultado = ventaService.findByUsuarioId(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("cliente1", resultado.get(0).getUsuario().getUsername());
        verify(ventaRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void findByUsuarioId_UsuarioSinVentas_RetornaListaVacia() {
        when(ventaRepository.findByUsuarioId(99L)).thenReturn(List.of());

        List<Venta> resultado = ventaService.findByUsuarioId(99L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(ventaRepository, times(1)).findByUsuarioId(99L);
    }
}