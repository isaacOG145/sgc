package utez.edu._b.sgc.users.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utez.edu._b.sgc.security.JwtUtil;
import utez.edu._b.sgc.users.model.UserDto;
import utez.edu._b.sgc.utils.Message;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class UserController {

    private final UserService userService;


    private final JwtUtil jwtUtil;


    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/findId/{userId}")
    public ResponseEntity<Message> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
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

    @PostMapping("/send-email")
    public ResponseEntity<Message> sendEmail(@Validated({UserDto.FindByEmail.class}) @RequestBody UserDto dto){
        return userService.sendEmail(dto);
    }


    @PutMapping("/change-pass")
    public ResponseEntity<Message> ChangePass(@Validated({UserDto.ChangePassword.class}) @RequestBody UserDto dto){
        return userService.ChangePassword(dto);
    }

}
