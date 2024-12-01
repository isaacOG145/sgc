package utez.edu._b.sgc.customer.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utez.edu._b.sgc.customer.model.CustomerDto;
import utez.edu._b.sgc.utils.Message;

@RestController
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/all")
    public ResponseEntity<Message> getAllCustomers() {
        return customerService.findAll();
    }

    @GetMapping("/active")
    public ResponseEntity<Message> getActiveCustomers() {
        return customerService.findActiveCustomers();
    }

    @PostMapping("/save")
    public ResponseEntity<Message> saveCustomers(@Validated(CustomerDto.Register.class) @RequestBody CustomerDto dto) {
        return customerService.save(dto);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> updateCustomers(@Validated(CustomerDto.Modify.class) @RequestBody CustomerDto dto) {
        return customerService.update(dto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Message> changeStatus(@Validated(CustomerDto.ChangeStatus.class) @RequestBody CustomerDto dto) {
        return customerService.changeStatus(dto);
    }

}
