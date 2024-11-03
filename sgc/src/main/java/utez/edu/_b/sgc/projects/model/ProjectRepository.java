package utez.edu._b.sgc.projects.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import utez.edu._b.sgc.customer.model.Customer;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    //Buscar todos los activos
    List<Project> findByStatus(boolean status);
}