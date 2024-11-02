package utez.edu._b.sgc.customer.control;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.customer.model.CustomerDto;
import utez.edu._b.sgc.customer.model.CustomerRepository;
import utez.edu._b.sgc.utils.Message;
import utez.edu._b.sgc.utils.TypesResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    //validar datos
    private void validateCustomerData(CustomerDto dto) {
        if (dto.getName().length() > 100) {
            throw new IllegalArgumentException("El nombre excede 100 caracteres");
        }
        if (!dto.getName().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("No se permiten caracteres especiales");
        }
        if (dto.getEmail().length() > 100) {
            throw new IllegalArgumentException("El correo excede 100 caracteres");
        }
        if (dto.getPhone().length() > 10) {
            throw new IllegalArgumentException("El número excede 10 caracteres");
        }
    }

    //Encontrar todo
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<Customer> customers = customerRepository.findAll();
        logger.info("La búsqueda ha sido realizada correctamente");
        return new ResponseEntity<>(new Message(customers,"Lista de clientes", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Emcontrar todos los activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findActiveCustomers() {
        List<Customer> activeCustomers = customerRepository.findByStatus(true);
        logger.info("Búsqueda de clientes activos realizada correctamente");
        return new ResponseEntity<>(new Message(activeCustomers, "Lista de clientes activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(CustomerDto dto) {
        try {
            validateCustomerData(dto); // Llama al método de validación

            Customer newCustomer = new Customer(dto.getName(), dto.getEmail(), dto.getPhone(), true);
            newCustomer = customerRepository.saveAndFlush(newCustomer);

            return new ResponseEntity<>(new Message(newCustomer, "Cliente guardado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Maneja las excepciones lanzadas por la validación
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al guardar el cliente", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    //actualizar cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(CustomerDto dto) {
        Customer customer = customerRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("El cliente no existe"));

        validateCustomerData(dto);

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhone());

        try {
            customer = customerRepository.saveAndFlush(customer);
            return new ResponseEntity<>(new Message(customer, "Cliente actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al actualizar el cliente", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    //cambiar estado
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(CustomerDto dto) {
        // Buscar el cliente por ID, lanzando una excepción si no se encuentra
        Customer customer = customerRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("El cliente no existe"));

        customer.setStatus(!customer.isStatus());

        try {

            customer = customerRepository.saveAndFlush(customer);
            return new ResponseEntity<>(new Message(customer, "El estado del cliente actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al actualizar el estado del cliente", e);
            return new ResponseEntity<>(new Message("Intentelo denuevo mas tarde", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }


}
