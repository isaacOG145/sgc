package utez.edu._b.sgc.projects.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utez.edu._b.sgc.projects.model.ProjectDto;
import utez.edu._b.sgc.utils.Message;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/all")
    public ResponseEntity<Message> getAllProjects() {
        return projectService.findAll();
    }

    @GetMapping("/active")
    public ResponseEntity<Message> getActiveProjects() {
        return projectService.findActiveProjects();
    }

    @PostMapping("/save")
    public ResponseEntity<Message> saveProjects(@Validated(ProjectDto.Register.class) @RequestBody ProjectDto dto) {
        return projectService.save(dto);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> updateCustomers(@Validated(ProjectDto.Modify.class) @RequestBody ProjectDto dto) {
        return projectService.update(dto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Message> changeStatus(@Validated(ProjectDto.ChangeStatus.class) @RequestBody ProjectDto dto) {
        return projectService.changeStatus(dto);
    }


}
