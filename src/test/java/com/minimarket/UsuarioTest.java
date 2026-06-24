package com.minimarket


@ExtendWith(MockitoExtension.class)
public class UsuarioTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuariosServiceImpl usuariosService;

    private Usuario usuario;
    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        rolAdmin = new Rol();
        rolAdmin.setId(1L);
        rolAdmin.setNombre("ROLE_ADMINISTRADOR");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        usuario.setPassword("admin123");
        usuario.setRoles(Set.of(rolAdmin));
    }

    // --- Entidad ---

    @Test
    void crearUsuario_DatosValidos_AtributosCorrectos() {
        assertNotNull(usuario);
        assertEquals("admin", usuario.getUsername());
        assertEquals("admin123", usuario.getPassword());
        assertEquals(1, usuario.getRoles().size());
    }

    @Test
    void usuario_TieneRolAdministrador_RetornaTrue() {
        boolean tieneRol = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ROLE_ADMINISTRADOR"));
        assertTrue(tieneRol);
    }

    @Test
    void usuario_NoTieneRolCliente_RetornaFalse() {
        boolean tieneRol = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ROLE_CLIENTE"));
        assertFalse(tieneRol);
    }

    // --- Service con Mockito ---

    @Test
    void findByUsername_UsuarioExistente_RetornaUsuario() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findByUsername("admin");

        assertTrue(resultado.isPresent());
        assertEquals("admin", resultado.get().getUsername());
        verify(usuarioRepository, times(1)).findByUsername("admin");
    }

    @Test
    void findByUsername_UsuarioNoExistente_RetornaVacio() {
        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.findByUsername("noexiste");

        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findByUsername("noexiste");
    }

    @Test
    void save_UsuarioValido_RetornaUsuarioGuardado() {
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.save(usuario);

        assertNotNull(resultado);
        assertEquals("admin", resultado.getUsername());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void findById_UsuarioNoExistente_RetornaVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.findById(99L);

        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findById(99L);
    }
}    