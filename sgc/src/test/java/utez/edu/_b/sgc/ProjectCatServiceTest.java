package utez.edu._b.sgc;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import utez.edu._b.sgc.projectCat.control.ProjectCatController;
import utez.edu._b.sgc.projectCat.control.ProjectCatService;
import utez.edu._b.sgc.projectCat.model.ProjectCatDTO;
import utez.edu._b.sgc.utils.Message;
import utez.edu._b.sgc.utils.TypesResponse;

class ProjectCatServiceTest {

    @Mock
    private ProjectCatService pcategoriesService;

    @InjectMocks
    private ProjectCatController projectCatController;

    // Inicializamos los mocks antes de cada prueba
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Asegura la inicialización de los mocks
    }

    @Test
    void testGetAllPCategories() {
        // Preparar el comportamiento simulado
        ResponseEntity<Message> response = new ResponseEntity<>(new Message("test", "Lista de categorías de proyectos", TypesResponse.SUCCESS), HttpStatus.OK);
        when(pcategoriesService.findAll()).thenReturn(response);

        // Llamar al método del controlador
        ResponseEntity<Message> result = projectCatController.getAllPCategories();

        // Verificar que el resultado es el esperado
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());

        // Aquí estamos verificando el valor correcto en el campo "getText()" de Message
        assertEquals("Lista de categorías de proyectos", result.getBody().getText());  // Cambiar .getResult() por .getText()

        // Verificar que se llamó al servicio
        verify(pcategoriesService).findAll();
    }


    @Test
    void testGetActiveCategories() {
        // Preparar el comportamiento simulado
        ResponseEntity<Message> response = new ResponseEntity<>(new Message("test", "Lista de categorías de proyectos activos", TypesResponse.SUCCESS), HttpStatus.OK);
        when(pcategoriesService.findActiveCustomers()).thenReturn(response);

        // Llamar al método del controlador
        ResponseEntity<Message> result = projectCatController.getActiveCategories();

        // Verificar que el resultado es el esperado
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Lista de categorías de proyectos activos", result.getBody().getText());
        verify(pcategoriesService).findActiveCustomers();  // Verificar que se llamó al servicio
    }

    @Test
    void testSaveCategories() {
        // Preparar el comportamiento simulado
        ProjectCatDTO dto = new ProjectCatDTO();
        dto.setName("Category 1");
        dto.setDescription("Category Description");

        ResponseEntity<Message> response = new ResponseEntity<>(new Message("test", "Categoría de proyecto guardada exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        when(pcategoriesService.save(any(ProjectCatDTO.class))).thenReturn(response);

        // Llamar al método del controlador
        ResponseEntity<Message> result = projectCatController.saveCategories(dto);

        // Verificar que el resultado es el esperado
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Categoría de proyecto guardada exitosamente", result.getBody().getText());
        verify(pcategoriesService).save(any(ProjectCatDTO.class));  // Verificar que se llamó al servicio
    }

    @Test
    void testUpdateCategories() {
        // Preparar el comportamiento simulado
        ProjectCatDTO dto = new ProjectCatDTO();
        dto.setId(1L);
        dto.setName("Updated Category");
        dto.setDescription("Updated Description");

        ResponseEntity<Message> response = new ResponseEntity<>(new Message("test", "Categoría de proyecto actualizada exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        when(pcategoriesService.update(any(ProjectCatDTO.class))).thenReturn(response);

        // Llamar al método del controlador
        ResponseEntity<Message> result = projectCatController.updateCategories(dto);

        // Verificar que el resultado es el esperado
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Categoría de proyecto actualizada exitosamente", result.getBody().getText());
        verify(pcategoriesService).update(any(ProjectCatDTO.class));  // Verificar que se llamó al servicio
    }

    @Test
    void testChangeStatus() {
        // Preparar el comportamiento simulado
        ProjectCatDTO dto = new ProjectCatDTO();
        dto.setId(1L);

        ResponseEntity<Message> response = new ResponseEntity<>(new Message("test", "El estado de la categoría de proyecto actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        when(pcategoriesService.changeStatus(any(ProjectCatDTO.class))).thenReturn(response);

        // Llamar al método del controlador
        ResponseEntity<Message> result = projectCatController.changeStatus(dto);

        // Verificar que el resultado es el esperado
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("El estado de la categoría de proyecto actualizado exitosamente", result.getBody().getText());
        verify(pcategoriesService).changeStatus(any(ProjectCatDTO.class));  // Verificar que se llamó al servicio
    }
}
