package utez.edu._b.sgc.customer.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.customer.model.CustomerDto;
import utez.edu._b.sgc.customer.model.CustomerRepository;
import utez.edu._b.sgc.utils.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {


    @InjectMocks
    private CustomerService customerService; // El servicio que estamos probando

    @Mock
    private CustomerRepository customerRepository; // Mock de la dependencia

    private CustomerDto customerDto;

    @BeforeEach
    public void setUp() {

        customerDto = new CustomerDto();
        //el ID se asigna automaticamente
        customerDto.setName("John Doe");
        customerDto.setEmail("johndoe@example.com");
        customerDto.setPhone("1234567890");
        //el estado es automatico como true
    }

    //nombre invalido
    @Test
    public void testCP001() {
        // Crear un DTO con un nombre que contiene caracteres especiales
        customerDto.setName("John@Doe");  // Nombre con un '@' no permitido

        // Ejecutar el método de guardado
        ResponseEntity<Message> response = customerService.save(customerDto);

        // Verificar que la respuesta es un error
        assertEquals(400, response.getStatusCodeValue()); // HTTP 400 Bad Request

        // Verificar que el mensaje de error es el correcto
        assertEquals("El nombre no puede contener caracteres especiales", response.getBody().getText());

    }

    //guardar un cliente
    @Test
    public void testCP002() {
        // Crear el cliente que esperamos guardar
        Customer mockCustomer = new Customer("John Doe", "johndoe@example.com", "1234567890", true);

        // Mock del repositorio para devolver el cliente guardado
        when(customerRepository.saveAndFlush(any(Customer.class))).thenReturn(mockCustomer);

        // Ejecutar el método de guardado
        ResponseEntity<Message> response = customerService.save(customerDto);

        // Verificar que la respuesta es correcta
        assertEquals(200, response.getStatusCodeValue()); // HTTP 200 OK
        assertEquals("Cliente guardado exitosamente", response.getBody().getText());
    }

    //numero de telefono que excede los caracteres
    @Test
    public void testCP003() {
        // Crear un DTO con teléfono inválido (más de 10 caracteres)
        customerDto.setPhone("123456789012");

        // Ejecutar el método de guardado
        ResponseEntity<Message> response = customerService.save(customerDto);

        // Verificar que la respuesta es un error
        assertEquals(400, response.getStatusCodeValue()); // HTTP 400 Bad Request
        assertEquals("El número excede 10 caracteres", response.getBody().getText());
    }

    //numero de telefono invalido
    @Test
    public void testCP004() {
        // Crear un DTO con teléfono inválido (no contiene solo números)
        customerDto.setPhone("telefono");  // Un valor no numérico

        // Ejecutar el método de guardado
        ResponseEntity<Message> response = customerService.save(customerDto);

        // Verificar que la respuesta es un error
        assertEquals(400, response.getStatusCodeValue()); // HTTP 400 Bad Request

        // Verificar que el mensaje de error es el correcto
        assertEquals("El teléfono solo puede contener números", response.getBody().getText());
    }
    //prueba para correo invalido
    @Test
    public void testCP005() {
        // Crear un DTO con un correo electrónico inválido
        customerDto.setEmail("correo@.com");  // Correo electrónico con formato inválido

        // Ejecutar el método de guardado
        ResponseEntity<Message> response = customerService.save(customerDto);

        // Verificar que la respuesta es un error
        assertEquals(400, response.getStatusCodeValue()); // HTTP 400 Bad Request

        // Verificar que el mensaje de error es el correcto
        assertEquals("Debe ser un correo electrónico válido", response.getBody().getText());

    }

    //prubas de actuañozar

    @Test
    public void testCP101() {
        // Cliente inicial con datos previos
        Customer existingCustomer = new Customer("John Doe", "johndoe@example.com", "1234567890", true);
        existingCustomer.setId(1L);  // Suponemos que este es un cliente con ID 1

        // DTO con nuevos datos
        customerDto.setName("John Updated");
        customerDto.setEmail("johnupdated@example.com");
        customerDto.setPhone("0987654321");

        // Mock del repositorio para devolver el cliente existente cuando se busque por ID
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(existingCustomer));
        when(customerRepository.saveAndFlush(any(Customer.class))).thenReturn(existingCustomer);

        // Ejecutar el método de actualización
        ResponseEntity<Message> response = customerService.update(customerDto);

        // Verificar que la respuesta es correcta
        assertEquals(200, response.getStatusCodeValue()); // HTTP 200 OK
        assertEquals("Cliente actualizado exitosamente", response.getBody().getText());
        assertEquals("John Updated", existingCustomer.getName());  // Verificamos que el nombre se actualizó
        assertEquals("johnupdated@example.com", existingCustomer.getEmail());  // Verificamos que el correo se actualizó
    }

    @Test
    public void testCP102() {
        // DTO con un ID que no existe
        customerDto.setId(999L);  // ID que no existe

        // Mock del repositorio para devolver un Optional vacío
        when(customerRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        // Ejecutar el método de actualización
        ResponseEntity<Message> response = customerService.update(customerDto);

        // Verificar que la respuesta es un error
        assertEquals(400, response.getStatusCodeValue()); // HTTP 400 Bad Request
        assertEquals("El cliente no existe", response.getBody().getText());  // Mensaje de error adecuado
    }

    @Test
    public void testCP103() {
        // Cliente existente
        Customer existingCustomer = new Customer("John Doe", "johndoe@example.com", "1234567890", true);
        existingCustomer.setId(1L);

        // DTO con teléfono inválido (más de 10 caracteres)
        customerDto.setPhone("123456789012");  // Teléfono con más de 10 caracteres

        // Mock del repositorio para devolver el cliente existente
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(existingCustomer));

        // Ejecutar el método de actualización
        ResponseEntity<Message> response = customerService.update(customerDto);

        // Verificar que la respuesta es un error
        assertEquals(400, response.getStatusCodeValue()); // HTTP 400 Bad Request
        assertEquals("El número excede 10 caracteres", response.getBody().getText());
    }

    @Test
    public void testCP104() {
        // Cliente existente
        Customer existingCustomer = new Customer("John Doe", "johndoe@example.com", "1234567890", true);
        existingCustomer.setId(1L);

        // DTO con teléfono no numérico
        customerDto.setId(1L);  // ID del cliente existente
        customerDto.setPhone("telefono");  // Teléfono no numérico

        // Mock del repositorio para devolver el cliente existente
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(existingCustomer));

        // Ejecutar el método de actualización
        ResponseEntity<Message> response = customerService.update(customerDto);

        // Verificar que la respuesta es un error
        assertEquals(400, response.getStatusCodeValue()); // HTTP 400 Bad Request
        assertEquals("El teléfono solo puede contener números", response.getBody().getText());
    }

    @Test
    public void testCP105() {
        // Cliente existente
        Customer existingCustomer = new Customer("John Doe", "johndoe@example.com", "1234567890", true);
        existingCustomer.setId(1L);

        // DTO con correo inválido
        customerDto.setId(1L);  // ID del cliente existente
        customerDto.setEmail("correo@.com");  // Correo con formato inválido

        // Mock del repositorio para devolver el cliente existente
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(existingCustomer));

        // Ejecutar el método de actualización
        ResponseEntity<Message> response = customerService.update(customerDto);

        // Verificar que la respuesta es un error
        assertEquals(400, response.getStatusCodeValue()); // HTTP 400 Bad Request
        assertEquals("Debe ser un correo electrónico válido", response.getBody().getText());
    }

    @Test
    public void testCP106() {
        // Crear un cliente mock con estado "true"
        Customer mockCustomer = new Customer("John Doe", "johndoe@example.com", "1234567890", true);
        mockCustomer.setId(1L);

        // Crear un DTO con el ID del cliente a cambiar el estado
        customerDto.setId(1L);

        // Mock del repositorio para devolver el cliente cuando se busque por ID
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(mockCustomer));

        // Mock del repositorio para guardar el cliente actualizado
        when(customerRepository.saveAndFlush(any(Customer.class))).thenReturn(mockCustomer);

        // Ejecutar el método de cambio de estado
        ResponseEntity<Message> response = customerService.changeStatus(customerDto);

        // Verificar que el estado ha cambiado
        assertFalse(mockCustomer.isStatus()); // El estado debería haberse cambiado a "false"

        // Verificar que la respuesta es la esperada
        assertEquals(200, response.getStatusCodeValue()); // HTTP 200 OK
        assertEquals("El estado del cliente actualizado exitosamente", response.getBody().getText());
    }

    @Test
    public void testCP107() {
        // Crear un DTO con un ID que no existe
        customerDto.setId(999L);  // Suponiendo que no existe un cliente con ID 999

        // Mock del repositorio para devolver un Optional vacío
        when(customerRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        // Ejecutar el método de cambio de estado
        ResponseEntity<Message> response = customerService.changeStatus(customerDto);

        // Verificar que la respuesta es un error
        assertEquals(400, response.getStatusCodeValue()); // HTTP 400 Bad Request
        assertEquals("El cliente no existe", response.getBody().getText());
    }

    @Test
    public void testChangeStatus_toActive() {
        // Crear un cliente mock con estado "false"
        Customer mockCustomer = new Customer("Jane Doe", "janedoe@example.com", "0987654321", false);
        mockCustomer.setId(2L);

        // Crear un DTO con el ID del cliente a cambiar el estado
        customerDto.setId(2L);

        // Mock del repositorio para devolver el cliente cuando se busque por ID
        when(customerRepository.findById(2L)).thenReturn(java.util.Optional.of(mockCustomer));

        // Mock del repositorio para guardar el cliente actualizado
        when(customerRepository.saveAndFlush(any(Customer.class))).thenReturn(mockCustomer);

        // Ejecutar el método de cambio de estado
        ResponseEntity<Message> response = customerService.changeStatus(customerDto);

        // Verificar que el estado ha cambiado a "true"
        assertTrue(mockCustomer.isStatus()); // El estado debería haberse cambiado a "true"

        // Verificar que la respuesta es la esperada
        assertEquals(200, response.getStatusCodeValue()); // HTTP 200 OK
        assertEquals("El estado del cliente actualizado exitosamente", response.getBody().getText());
    }




}