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
            throw new IllegalArgumentException("El nombre no puede exceder los 100 caracteres");
        }
        if (dto.getEmail().length() > 100) {
            throw new IllegalArgumentException("El correo no puede exceder los 100 caracteres");
        }
        if (!dto.getName().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y caracteres especiales permitidos (acentos, ñ y espacios)");
        }
        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El correo debe tener un formato válido");
        }
        if (dto.getPhone().length() > 10) {
            throw new IllegalArgumentException("El número de teléfono no puede exceder los 10 caracteres");
        }
        if (!dto.getPhone().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("El teléfono debe contener solo 10 dígitos numéricos");
        }
    }


    //Encontrar todo
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            logger.info("No se encontraron clientes.");
            return new ResponseEntity<>(new Message("No se encontraron clientes", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("La búsqueda ha sido realizada correctamente");
        return new ResponseEntity<>(new Message(customers, "Lista de clientes", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Encontrar todos los activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findActiveCustomers() {
        List<Customer> activeCustomers = customerRepository.findByStatus(true);
        if (activeCustomers.isEmpty()) {
            logger.info("No se encontraron clientes activos.");
            return new ResponseEntity<>(new Message("No se encontraron clientes activos", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Búsqueda de clientes activos realizada correctamente");
        return new ResponseEntity<>(new Message(activeCustomers, "Lista de clientes activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    //guardar cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(CustomerDto dto) {
        try {

            validateCustomerData(dto);

            if (customerRepository.existsByEmail(dto.getEmail())) {
                return new ResponseEntity<>(new Message("El correo electrónico ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            if (customerRepository.existsByPhoneNumber(dto.getPhone())) {
                return new ResponseEntity<>(new Message("El número de teléfono ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            Customer newCustomer = new Customer(dto.getName(), dto.getEmail(), dto.getPhone(), true);
            newCustomer = customerRepository.saveAndFlush(newCustomer);

            return new ResponseEntity<>(new Message(newCustomer, "Cliente guardado exitosamente", TypesResponse.SUCCESS), HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            logger.error("Error al guardar el cliente", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //actualizar cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(CustomerDto dto) {
        try {

            Customer customer = customerRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("El cliente no existe"));

            validateCustomerData(dto);

            if (!customer.getEmail().equals(dto.getEmail()) && customerRepository.existsByEmail(dto.getEmail())) {
                return new ResponseEntity<>(new Message("El correo electrónico ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            if (!customer.getPhoneNumber().equals(dto.getPhone()) && customerRepository.existsByPhoneNumber(dto.getPhone())) {
                return new ResponseEntity<>(new Message("El número de teléfono ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            customer.setName(dto.getName());
            customer.setEmail(dto.getEmail());
            customer.setPhoneNumber(dto.getPhone());

            customer = customerRepository.saveAndFlush(customer);

            return new ResponseEntity<>(new Message(customer, "Cliente actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al actualizar el cliente", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //cambiar estado
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(CustomerDto dto) {
        try {

            Customer customer = customerRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("El cliente no existe"));

            customer.setStatus(!customer.isStatus());
            customer = customerRepository.saveAndFlush(customer);

            return new ResponseEntity<>(new Message(customer, "El estado del cliente actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Cliente no encontrado para cambiar estado", e);
            return new ResponseEntity<>(new Message("El cliente no existe", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error al actualizar el estado del cliente", e);
            return new ResponseEntity<>(new Message("Intentelo de nuevo más tarde", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
