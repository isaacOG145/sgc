package utez.edu._b.sgc.customer.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.customer.model.CustomerDto;
import utez.edu._b.sgc.customer.model.CustomerRepository;
import utez.edu._b.sgc.utils.Message;
import utez.edu._b.sgc.utils.TypesResponse;

public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //guardar un cliente
    @Test
    public void testCP001() {

        CustomerDto dto = new CustomerDto();
        dto.setName("Ivan");
        dto.setEmail("ivan@example.com");
        dto.setPhone("1234567890");


        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setName(dto.getName());

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        ResponseEntity<Message> response = customerService.save(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cliente guardado exitosamente", response.getBody().getText());
        assertEquals(TypesResponse.SUCCESS, response.getBody().getType());
    }

    //guardar un cliente con correo invalido
    @Test
    public void testCP002() {

        CustomerDto dto = new CustomerDto();
        dto.setName("Ivan");
        dto.setEmail("ivan.example.com");
        dto.setPhone("1234567890");

        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setName(dto.getName());

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        ResponseEntity<Message> response = customerService.save(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Debe ser un correo electrónico válido", response.getBody().getText());
        assertEquals(TypesResponse.ERROR, response.getBody().getType());
    }

    //guardar un cliente con telefono invalido
    @Test
    public void testCP003() {

        CustomerDto dto = new CustomerDto();
        dto.setName("Ivan");
        dto.setEmail("ivan@example.com");
        dto.setPhone("telefono");

        ResponseEntity<Message> response = customerService.save(dto);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El teléfono solo puede contener números", response.getBody().getText());
        assertEquals(TypesResponse.ERROR, response.getBody().getType());
    }

}
