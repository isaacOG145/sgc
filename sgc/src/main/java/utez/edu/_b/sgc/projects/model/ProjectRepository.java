package utez.edu._b.sgc.projects.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import utez.edu._b.sgc.customer.model.Customer;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    //Buscar todos los activos
    List<Project> findByStatus(boolean status);

    List<Project> findAll();

    // Métodos para verificar duplicados
    boolean existsByName(String name);

    boolean existsByAbbreviation(String abbreviation);
}
