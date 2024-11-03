package utez.edu._b.sgc.projectCat.control;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu._b.sgc.projectCat.model.ProjectCategory;
import utez.edu._b.sgc.projectCat.model.ProjectCatDTO;
import utez.edu._b.sgc.projectCat.model.ProjectCatRepository;
import utez.edu._b.sgc.utils.Message;
import utez.edu._b.sgc.utils.TypesResponse;

import java.sql.SQLException;
import java.util.List;


@Transactional
@Service
public class ProjectCatService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectCatService.class);

    private final ProjectCatRepository pcategoriesRepository;

    @Autowired
    public ProjectCatService(ProjectCatRepository pcategoriesRepository) {
        this.pcategoriesRepository = pcategoriesRepository;
    }

    //validar datos
    private void validatePCategoriesData(ProjectCatDTO dto) {
        if (dto.getName().length() > 100) {
            throw new IllegalArgumentException("El nombre excede 100 caracteres");
        }
        if (!dto.getName().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("No se permiten caracteres especiales");
        }
        if (dto.getDescription().length() > 100) {
            throw new IllegalArgumentException("La descripcion excede 500 caracteres");
        }
        if (!dto.getDescription().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("No se permiten caracteres especiales");
        }

    }

    //Encontrar todo
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<ProjectCategory> pcategories = pcategoriesRepository.findAll();
        logger.info("La búsqueda ha sido realizada correctamente");
        return new ResponseEntity<>(new Message(pcategories,"Lista de las categorías de proyectos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Emcontrar todos los activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findActiveCustomers() {
        List<ProjectCategory> activePCategories = pcategoriesRepository.findByStatus(true);
        logger.info("Búsqueda de las categorías de proyectos activos realizada correctamente");
        return new ResponseEntity<>(new Message(activePCategories, "Lista de las categorías de proyectos activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(ProjectCatDTO dto) {
        try {
            validatePCategoriesData(dto); // Llama al método de validación

            ProjectCategory newProjectCat = new ProjectCategory(dto.getName(), dto.getDescription(), true);
            newProjectCat = pcategoriesRepository.saveAndFlush(newProjectCat);

            return new ResponseEntity<>(new Message(newProjectCat, "Categoría de proyecto guardada exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Maneja las excepciones lanzadas por la validación
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al guardar la categoría de proyecto", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    //actualizar cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(ProjectCatDTO dto) {
        ProjectCategory pcategories = pcategoriesRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("La categoría de proyecto no existe"));

        validatePCategoriesData(dto);

        pcategories.setName(dto.getName());
        pcategories.setDescription(dto.getDescription());

        try {
            pcategories = pcategoriesRepository.saveAndFlush(pcategories);
            return new ResponseEntity<>(new Message(pcategories, "Categoría de proyecto a sido actualizada exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al actualizar la categoría de proyecto", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    //cambiar estado
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(ProjectCatDTO dto) {
        // Buscar el cliente por ID, lanzando una excepción si no se encuentra
        ProjectCategory pcategories = pcategoriesRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("La categoría de proyecto no existe"));

        pcategories.setStatus(!pcategories.isStatus());

        try {

            pcategories = pcategoriesRepository.saveAndFlush(pcategories);
            return new ResponseEntity<>(new Message(pcategories, "El estado de la categoría de proyecto a sido actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al actualizar el estado de la categoría de proyecto", e);
            return new ResponseEntity<>(new Message("Intentelo denuevo mas tarde", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }


}
