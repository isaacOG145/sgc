package utez.edu._b.sgc;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.mock.mockito.MockBean;
import utez.edu._b.sgc.customer.control.CustomerService;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.customer.model.CustomerDto;
import utez.edu._b.sgc.customer.model.CustomerRepository;
import utez.edu._b.sgc.utils.Message;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;

    // Prueba de guardar un cliente
    @Test
    public void testSaveCustomerSuccess() {
        // Setup
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("John Doe");
        customerDto.setEmail("john.doe@example.com");
        customerDto.setPhone("1234567890");

        Customer customer = new Customer("John Doe", "john.doe@example.com", "1234567890", true);

        when(customerRepository.saveAndFlush(any(Customer.class))).thenReturn(customer);

        // Ejecución
        ResponseEntity<Message> response = customerService.save(customerDto);

        // Verificación
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getText().contains("Cliente guardado exitosamente"));
    }
    @Test
    public void testUpdateCustomerSuccess() {
        // Setup
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerDto.setName("Jane Doe");
        customerDto.setEmail("jane.doe@example.com");
        customerDto.setPhone("0987654321");

        Customer existingCustomer = new Customer("Old Name", "old.email@example.com", "9876543210", true);
        existingCustomer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(existingCustomer));
        when(customerRepository.saveAndFlush(any(Customer.class))).thenReturn(existingCustomer);

        // Ejecución
        ResponseEntity<Message> response = customerService.update(customerDto);

        // Verificación
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getText().contains("Cliente actualizado exitosamente"));
    }
    @Test
    public void testUpdateCustomerNotFound() {
        // Setup
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(999L); // Cliente no existente
        customerDto.setName("Nonexistent Customer");
        customerDto.setEmail("nonexistent@example.com");
        customerDto.setPhone("0000000000");

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Ejecución y verificación de la excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> customerService.update(customerDto));

        // Verificación de la excepción
        assertEquals("El cliente no existe", exception.getMessage());
    }

    @Test
    public void testFindActiveCustomersSuccess() {
        // Setup
        Customer activeCustomer1 = new Customer("John Doe", "john.doe@example.com", "1234567890", true);
        Customer activeCustomer2 = new Customer("Jane Doe", "jane.doe@example.com", "0987654321", true);
        List<Customer> activeCustomers = List.of(activeCustomer1, activeCustomer2);

        when(customerRepository.findByStatus(true)).thenReturn(activeCustomers);

        // Ejecución
        ResponseEntity<Message> response = customerService.findActiveCustomers();

        // Verificación
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getText().contains("Lista de clientes activos"));
        assertEquals(2, ((List<Customer>) response.getBody().getResult()).size());  // Verificar que la lista tenga dos clientes activos
    }

    @Test
    public void testChangeCustomerStatusSuccess() {
        // Setup
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerDto.setName("John Doe");
        customerDto.setEmail("john.doe@example.com");
        customerDto.setPhone("1234567890");

        Customer existingCustomer = new Customer("John Doe", "john.doe@example.com", "1234567890", true);
        existingCustomer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(existingCustomer));
        when(customerRepository.saveAndFlush(any(Customer.class))).thenReturn(existingCustomer);

        // Ejecución
        ResponseEntity<Message> response = customerService.changeStatus(customerDto);

        // Verificación
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getText().contains("El estado del cliente actualizado exitosamente"));
        assertFalse(((Customer) response.getBody().getResult()).isStatus());  // Verificar que el estado cambió a inactivo
    }


}
