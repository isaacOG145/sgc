package utez.edu._b.sgc.projects.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.customer.model.CustomerRepository;
import utez.edu._b.sgc.projectCat.model.ProjectCatDTO;
import utez.edu._b.sgc.projectCat.model.ProjectCatRepository;
import utez.edu._b.sgc.projectCat.model.ProjectCategory;
import utez.edu._b.sgc.projects.model.Project;
import utez.edu._b.sgc.projects.model.ProjectDto;
import utez.edu._b.sgc.projects.model.ProjectRepository;
import utez.edu._b.sgc.utils.Message;
import utez.edu._b.sgc.utils.TypesResponse;

import java.sql.SQLException;
import java.util.List;

@Transactional
@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;
    private final ProjectCatRepository projectCategoryRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            CustomerRepository customerRepository,
            ProjectCatRepository projectCategoryRepository
    ) {

        this.projectRepository = projectRepository;
        this.customerRepository = customerRepository;
        this.projectCategoryRepository = projectCategoryRepository;
    }

    //validar datos
    private void validateProjectData(ProjectDto dto) {

        if (dto.getName().length() > 50) {
            throw new IllegalArgumentException("El nombre excede los 50 caracteres");
        }
        if (!dto.getName().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("El nombre no puede contener cáracteres especiales");
        }
        if (dto.getAbbreviation().length() > 10) {
            throw new IllegalArgumentException("La abreviación excede los 10 caracteres");
        }
        if (!dto.getAbbreviation().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$")) {
            throw new IllegalArgumentException("La abreviación no puede contener cáracteres especiales");
        }
        if (dto.getDescription().length() > 255) {
            throw new IllegalArgumentException("La descripción excede los 255 caracteres");
        }
        if (!dto.getDescription().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("La descripción no puede contener cáracteres especiales");
        }
    }

    private void validateCustomerIsActive(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        if (!customer.isStatus()) {
            throw new IllegalArgumentException("El proyecto no está activo");
        }
    }

    private void validateProjectCategoryIsActive(Long projectCategoryId) {
        ProjectCategory projectCategory = projectCategoryRepository.findById(projectCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        if (!projectCategory.isStatus()) {
            throw new IllegalArgumentException("El proyecto no está activo");
        }
    }

    // Encontrar todo
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<Project> projects = projectRepository.findAll();

        if (projects.isEmpty()) {
            logger.info("No se encontraron proyectos");
            return new ResponseEntity<>(new Message("No se encontraron proyectos", TypesResponse.WARNING), HttpStatus.OK);
        }

        logger.info("La búsqueda ha sido realizada correctamente");
        return new ResponseEntity<>(new Message(projects, "Lista de proyectos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Encontrar todos los activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findActiveProjects() {
        List<Project> activeProjects = projectRepository.findByStatus(true);

        if (activeProjects.isEmpty()) {
            logger.info("No se encontraron proyectos activos");
            return new ResponseEntity<>(new Message("No se encontraron proyectos activos", TypesResponse.WARNING), HttpStatus.OK);
        }

        logger.info("Búsqueda de proyectos activos realizada correctamente");
        return new ResponseEntity<>(new Message(activeProjects, "Lista de proyectos activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Guardar proyecto
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(ProjectDto dto) {
        try {
            validateProjectData(dto);

            validateCustomerIsActive(dto.getCustomerId());

            // Validar que la categoría del proyecto esté activa
            validateProjectCategoryIsActive(dto.getProjectCategoryId());

            if (projectRepository.existsByName(dto.getName())) {
                return new ResponseEntity<>(new Message("El nombre del proyecto ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            if (projectRepository.existsByAbbreviation(dto.getAbbreviation())) {
                return new ResponseEntity<>(new Message("La abreviación del proyecto ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            // Crear un nuevo proyecto
            Project newProject = new Project(
                    dto.getName(),
                    dto.getAbbreviation(),
                    dto.getDescription(),
                    new Customer(dto.getCustomerId()),
                    new ProjectCategory(dto.getProjectCategoryId()),
                    true);

            newProject = projectRepository.saveAndFlush(newProject);

            return new ResponseEntity<>(new Message(newProject, "Proyecto guardado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al guardar el Proyecto", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }


    // Actualizar proyecto
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(ProjectDto dto) {
        Project project = projectRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("El proyecto no existe"));

        validateProjectData(dto);

        validateCustomerIsActive(dto.getCustomerId());

        validateProjectCategoryIsActive(dto.getProjectCategoryId());

        if (!project.getName().equals(dto.getName()) && projectRepository.existsByName(dto.getName())) {
            return new ResponseEntity<>(new Message("El nombre del proyecto ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (!project.getAbbreviation().equals(dto.getAbbreviation()) && projectRepository.existsByAbbreviation(dto.getAbbreviation())) {
            return new ResponseEntity<>(new Message("La abreviación del proyecto ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        project.setName(dto.getName());
        project.setAbbreviation(dto.getAbbreviation());
        project.setDescription(dto.getDescription());
        project.setCustomer(new Customer(dto.getCustomerId()));
        project.setProjectCategory(new ProjectCategory(dto.getProjectCategoryId()));

        try {
            project = projectRepository.saveAndFlush(project);
            return new ResponseEntity<>(new Message(project, "Proyecto actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al actualizar el proyecto", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }


    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(ProjectDto dto) {
        try {

            Project project = projectRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("El proyecto no existe"));

            project.setStatus(!project.isStatus());

            project = projectRepository.saveAndFlush(project);

            return new ResponseEntity<>(new Message(project, "El estado del proyecto ha sido actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (RuntimeException e) {

            logger.error("Error al buscar el proyecto: {}", e.getMessage());
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {

            logger.error("Error inesperado al cambiar el estado del proyecto", e);
            return new ResponseEntity<>(new Message("Hubo un problema al cambiar el estado del proyecto, intente nuevamente.", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
