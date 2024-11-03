package utez.edu._b.sgc.projectCat.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectCatRepository extends JpaRepository<ProjectCategory, Long> {

    //Buscar todos los activos
    List<ProjectCategory> findByStatus(boolean status);
}
