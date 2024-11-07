package utez.edu._b.sgc.projects.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    //validar datos
    private void validateProjectData(ProjectDto dto) {

        if(dto.getName().length()> 50){
            throw new IllegalArgumentException("El nombre excede 50 caracteres");
        }
        if(dto.getAbbreviation().length()>10){
            throw new IllegalArgumentException("La abreviacion excede 10 caracteres");
        }

    }

    //Encontrar todo
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<Project> projects = projectRepository.findAll();
        logger.info("La búsqueda ha sido realizada correctamente");
        return new ResponseEntity<>(new Message(projects,"Lista de proyectos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Emcontrar todos los activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findActiveProjects() {
        List<Project> activeProjects = projectRepository.findByStatus(true);
        logger.info("Búsqueda de proyectos activos realizada correctamente");
        return new ResponseEntity<>(new Message(activeProjects, "Lista de proyectos activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar proyecto
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(ProjectDto dto) {
        try {
            validateProjectData(dto); // Llama al método de validación

            Project newProject = new Project(
                    dto.getName(),
                    dto.getAbbreviation(),
                    dto.getDescription(),
                    dto.getCustomer(),
                    dto.getProjectCategory(),true);
            newProject = projectRepository.saveAndFlush(newProject);

            return new ResponseEntity<>(new Message(newProject, "Proyecto guardado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Maneja las excepciones lanzadas por la validación
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al guardar el Proyecto", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    //actualizar cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(ProjectDto dto) {
        Project project = projectRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("El proyecto no existe"));

        validateProjectData(dto);

        project.setName(dto.getName());
        project.setAbbreviation(dto.getAbbreviation());
        project.setDescription(dto.getDescription());
        project.setCustomer(dto.getCustomer());
        project.setProjectCategory(dto.getProjectCategory());

        try {
            project = projectRepository.saveAndFlush(project);
            return new ResponseEntity<>(new Message(project, "Proyecto actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al actualizar el proyecto", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    //cambiar estado
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(ProjectDto dto) {
        // Buscar el cliente por ID, lanzando una excepción si no se encuentra
        Project project = projectRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("El Proyecto no existe"));

        project.setStatus(!project.isStatus());

        try {

            project = projectRepository.saveAndFlush(project);
            return new ResponseEntity<>(new Message(project, "El estado del proyecto actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al actualizar el estado del proyecto", e);
            return new ResponseEntity<>(new Message("Intentelo denuevo mas tarde", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }



}
