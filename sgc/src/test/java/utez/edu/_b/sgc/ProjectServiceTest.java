package utez.edu._b.sgc;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.projectCat.model.ProjectCategory;
import utez.edu._b.sgc.projects.control.ProjectController;
import utez.edu._b.sgc.projects.control.ProjectService;
import utez.edu._b.sgc.projects.model.Project;
import utez.edu._b.sgc.projects.model.ProjectDto;
import utez.edu._b.sgc.utils.Message;
import utez.edu._b.sgc.utils.TypesResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)  // Usamos MockitoExtension en lugar de initMocks
class ProjectServiceTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;
    @Test
    void testGetAllProjects() {
        // Mockea la respuesta del servicio con una lista vacía
        when(projectService.findAll()).thenReturn(ResponseEntity.ok(new Message(Collections.emptyList(), "Lista de proyectos", TypesResponse.SUCCESS)));

        // Llamada al controlador
        ResponseEntity<Message> response = projectController.getAllProjects();

        // Verificación de la respuesta
        assertNotNull(response, "La respuesta no debe ser nula");
        assertEquals(200, response.getStatusCodeValue(), "El código de estado debe ser 200");

        // Verifica que la lista de proyectos no esté vacía
        Message responseBody = response.getBody();
        assertNotNull(responseBody, "El cuerpo de la respuesta no debe ser nulo");

        // Asegúrate de que la lista de resultados sea de tipo correcto y no esté vacía
        assertTrue(responseBody.getResult() instanceof List, "El resultado debe ser una lista");
        List<?> resultList = (List<?>) responseBody.getResult();  // Hacemos el cast seguro
        assertTrue(resultList.isEmpty(), "La lista de proyectos debe estar vacía");  // Verifica que esté vacía
    }


    @Test
    void testGetActiveProjects() {
        // Crea una lista de proyectos activos de prueba
        Project activeProject1 = new Project();
        activeProject1.setName("Active Project 1");
        activeProject1.setDescription("Descripción del Proyecto Activo 1");

        Project activeProject2 = new Project();
        activeProject2.setName("Active Project 2");
        activeProject2.setDescription("Descripción del Proyecto Activo 2");

        // Mockea la respuesta del servicio para proyectos activos
        when(projectService.findActiveProjects()).thenReturn(ResponseEntity.ok(new Message(Arrays.asList(activeProject1, activeProject2), "Lista de proyectos activos", TypesResponse.SUCCESS)));

        // Llamada al controlador
        ResponseEntity<Message> response = projectController.getActiveProjects();

        // Verificación de la respuesta
        assertNotNull(response, "La respuesta no debe ser nula");
        assertEquals(200, response.getStatusCodeValue(), "El código de estado debe ser 200");

        // Verifica que la lista de proyectos activos no esté vacía
        Message responseBody = response.getBody();
        assertNotNull(responseBody, "El cuerpo de la respuesta no debe ser nulo");

        // Asegúrate de que la lista de resultados sea de tipo correcto y no esté vacía
        assertTrue(responseBody.getResult() instanceof List, "El resultado debe ser una lista");
        List<?> resultList = (List<?>) responseBody.getResult();  // Hacemos el cast seguro
        assertFalse(resultList.isEmpty(), "La lista de proyectos activos no debe estar vacía");  // Verifica que no esté vacía
    }


    @Test
    void testSaveProjects() {
        // Crea un DTO de prueba
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Project A");
        projectDto.setAbbreviation("PA");
        projectDto.setDescription("Description of Project A");
        projectDto.setCustomer(new Customer());  // Simulando un objeto cliente válido
        projectDto.setProjectCategory(new ProjectCategory());  // Simulando una categoría de proyecto válida

        // Mockea la respuesta del servicio para el guardado
        when(projectService.save(projectDto)).thenReturn(ResponseEntity.ok(new Message(new Project(), "Proyecto guardado exitosamente", TypesResponse.SUCCESS)));

        ResponseEntity<Message> response = projectController.saveProjects(projectDto);

        // Verificación de la respuesta
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Proyecto guardado exitosamente", response.getBody().getText());
    }

    @Test
    void testUpdateCustomers() {
        // Crea un DTO de prueba para actualizar
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("Updated Project");
        projectDto.setAbbreviation("UP");
        projectDto.setDescription("Updated description");
        projectDto.setCustomer(new Customer());
        projectDto.setProjectCategory(new ProjectCategory());

        // Mockea la respuesta del servicio para la actualización
        when(projectService.update(projectDto)).thenReturn(ResponseEntity.ok(new Message(new Project(), "Proyecto actualizado exitosamente", TypesResponse.SUCCESS)));

        ResponseEntity<Message> response = projectController.updateCustomers(projectDto);

        // Verificación de la respuesta
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Proyecto actualizado exitosamente", response.getBody().getText());
    }

    @Test
    void testChangeStatus() {
        // Crea un DTO de prueba para cambiar el estado
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);

        // Mockea la respuesta del servicio para cambiar el estado
        when(projectService.changeStatus(projectDto)).thenReturn(ResponseEntity.ok(new Message(new Project(), "El estado del proyecto actualizado exitosamente", TypesResponse.SUCCESS)));

        ResponseEntity<Message> response = projectController.changeStatus(projectDto);

        // Verificación de la respuesta
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("El estado del proyecto actualizado exitosamente", response.getBody().getText());
    }
}
