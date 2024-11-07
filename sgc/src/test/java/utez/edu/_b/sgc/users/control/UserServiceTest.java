package utez.edu._b.sgc.users.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import utez.edu._b.sgc.role.model.Role;
import utez.edu._b.sgc.role.model.RoleRepository;
import utez.edu._b.sgc.users.model.User;
import utez.edu._b.sgc.users.model.UserDto;
import utez.edu._b.sgc.users.model.UserRepository;
import utez.edu._b.sgc.utils.Message;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;


    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    //usuario sin rol
    @Test
    public void testCP001() {
        UserDto dto = new UserDto();
        dto.setName("Juan");
        dto.setLastName("Pérez");
        dto.setEmail("juan@example.com");
        dto.setPhoneNumber("1234567890");
        dto.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(dto.getName());
        savedUser.setLastName(dto.getLastName());
        savedUser.setEmail(dto.getEmail());

        // Simulación del repositorio
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);

        ResponseEntity<Message> response = userService.save(dto);

        // Verificar el estado de la respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario guardado exitosamente", response.getBody().getText());
    }

    //usuario con rol
    @Test
    public void testCP002() {
        // Configurar el DTO de usuario
        UserDto dto = new UserDto();
        dto.setName("Juan");
        dto.setLastName("Pérez");
        dto.setEmail("juan@example.com");
        dto.setPhoneNumber("1234567890");
        dto.setPassword("password123");
        // Ejemplo de asignación de roles

        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role2));

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(dto.getName());
        savedUser.setLastName(dto.getLastName());
        savedUser.setEmail(dto.getEmail());
        savedUser.setRoles(new HashSet<>(Arrays.asList(role1, role2)));

        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);

        ResponseEntity<Message> response = userService.save(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario guardado exitosamente", response.getBody().getText());

        assertNotNull(savedUser.getRoles());
        assertEquals(2, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().contains(role1));
        assertTrue(savedUser.getRoles().contains(role2));
    }


}