package utez.edu._b.sgc.users.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utez.edu._b.sgc.projectCat.model.ProjectCatDTO;
import utez.edu._b.sgc.users.model.UserDto;
import utez.edu._b.sgc.utils.Message;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<Message> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/active")
    public ResponseEntity<Message> getActiveUsers() {
        return userService.findActiveUsers();
    }

    @PostMapping("/save")
    public ResponseEntity<Message> saveUser(@Validated(UserDto.Register.class) @RequestBody UserDto dto) {
        return userService.save(dto);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> updateUser(@Validated(UserDto.Modify.class) @RequestBody UserDto dto) {
        return userService.update(dto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Message> changeStatus(@Validated(UserDto.ChangeStatus.class) @RequestBody UserDto dto) {
        return userService.changeStatus(dto);
    }


}
