package utez.edu._b.sgc.customer.model;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    //Buscar todos los activos
    List<Customer> findByStatus(boolean status);

    // Método para verificar si ya existe un correo electrónico
    boolean existsByEmail(String email);

    // Método para verificar si ya existe un número de teléfono
    boolean existsByPhoneNumber(String phoneNumber);

}
