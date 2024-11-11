package utez.edu._b.sgc.projectCat.control;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utez.edu._b.sgc.projectCat.model.ProjectCatDTO;
import utez.edu._b.sgc.utils.Message;


@RestController
@RequestMapping("/projectCat")
public class ProjectCatController {

    private final ProjectCatService pcategoriesService;

    @Autowired
    public ProjectCatController(ProjectCatService pcategoriesService) {
        this.pcategoriesService = pcategoriesService;
    }

    @GetMapping("/all")
    public ResponseEntity<Message> getAllPCategories() {
        return pcategoriesService.findAll();
    }

    @GetMapping("/active")
    public ResponseEntity<Message> getActiveCategories() {
        return pcategoriesService.findActive();
    }

    @PostMapping("/save")
    public ResponseEntity<Message> saveCategories(@Validated(ProjectCatDTO.Register.class) @RequestBody ProjectCatDTO dto) {
        return pcategoriesService.save(dto);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> updateCategories(@Validated(ProjectCatDTO.Modify.class) @RequestBody ProjectCatDTO dto) {
        return pcategoriesService.update(dto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Message> changeStatus(@Validated(ProjectCatDTO.ChangeStatus.class) @RequestBody ProjectCatDTO dto) {
        return pcategoriesService.changeStatus(dto);
    }

}

