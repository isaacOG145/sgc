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
public class  ProjectCatService {
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
            throw new IllegalArgumentException("La descripcion excede 100 caracteres");
        }
        if(!dto.getDescription().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")){
            throw new IllegalArgumentException("No se permiten caracteres especiales");
        }

    }

    //Encontrar todo
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<ProjectCategory> pcategories = pcategoriesRepository.findAll();

        if (pcategories.isEmpty()) {
            logger.info("No se encontraron categorías de proyectos.");
            return new ResponseEntity<>(new Message("No se encontraron categorías de proyectos", TypesResponse.WARNING), HttpStatus.OK);
        }

        logger.info("La búsqueda ha sido realizada correctamente");
        return new ResponseEntity<>(new Message(pcategories, "Lista de las categorías de proyectos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Emcontrar todos los activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findActive() {
        List<ProjectCategory> activePCategories = pcategoriesRepository.findByStatus(true);

        if (activePCategories.isEmpty()) {
            // Si no hay categorías activas, retornamos un mensaje de advertencia
            logger.info("No se encontraron categorías de proyectos activos.");
            return new ResponseEntity<>(new Message("No se encontraron categorías de proyectos activos", TypesResponse.WARNING), HttpStatus.OK);
        }

        logger.info("Búsqueda de las categorías de proyectos activos realizada correctamente");
        return new ResponseEntity<>(new Message(activePCategories, "Lista de las categorías de proyectos activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    //Guardar categoria
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(ProjectCatDTO dto) {
        try {

            validatePCategoriesData(dto);

            if (pcategoriesRepository.existsByName(dto.getName())) {
                return new ResponseEntity<>(new Message("El nombre de la categoría ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            ProjectCategory newProjectCat = new ProjectCategory(dto.getName(), dto.getDescription(), true);
            newProjectCat = pcategoriesRepository.saveAndFlush(newProjectCat);

            return new ResponseEntity<>(new Message(newProjectCat, "Categoría de proyecto guardada exitosamente", TypesResponse.SUCCESS), HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al guardar la categoría de proyecto", e);
            return new ResponseEntity<>(new Message("Ocurrió un error al guardar la categoría de proyecto. Revise los datos e inténtelo de nuevo.", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //actualizar categoria
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(ProjectCatDTO dto) {
        try {

            ProjectCategory existingCategory = pcategoriesRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("La categoría de proyecto no existe"));

            validatePCategoriesData(dto);

            if (!existingCategory.getName().equals(dto.getName()) && pcategoriesRepository.existsByName(dto.getName())) {
                return new ResponseEntity<>(new Message("El nombre de la categoría ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            existingCategory.setName(dto.getName());
            existingCategory.setDescription(dto.getDescription());

            ProjectCategory updatedCategory = pcategoriesRepository.saveAndFlush(existingCategory);

            return new ResponseEntity<>(new Message(updatedCategory, "Categoría de proyecto actualizada exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {

            logger.error("Error al buscar la categoría de proyecto: {}", e.getMessage());
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            logger.error("Error al actualizar la categoría de proyecto", e);
            return new ResponseEntity<>(new Message("Ocurrió un error inesperado al intentar actualizar la categoría. Intente nuevamente más tarde.", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //cambiar estado
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(ProjectCatDTO dto) {
        try {

            ProjectCategory pcategories = pcategoriesRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("La categoría de proyecto no existe"));

            pcategories.setStatus(!pcategories.isStatus());

            pcategories = pcategoriesRepository.saveAndFlush(pcategories);

            return new ResponseEntity<>(new Message(pcategories, "El estado de la categoría de proyecto ha sido actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (RuntimeException e) {

            logger.error("Error al buscar la categoría de proyecto: {}", e.getMessage());
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
         catch (Exception e) {

            logger.error("Error inesperado al cambiar el estado de la categoría de proyecto", e);
            return new ResponseEntity<>(new Message("Hubo un problema al cambiar el estado de la categoría, intente nuevamente.", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
