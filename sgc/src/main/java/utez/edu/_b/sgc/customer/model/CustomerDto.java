package utez.edu._b.sgc.customer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CustomerDto {

    @NotNull(groups = {Modify.class,ChangeStatus.class},message = "El id no puede ser nulo")
    private Long id;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo nombre no puede estar vacio")
    private String name;

    @NotBlank(groups = {Modify.class, Register.class},message = "El correo no puede estar vacío")
    private String email;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo teléfono no puede estar vacío")
    private String phone;

    private boolean status;

    public CustomerDto() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public boolean isStatus() { return this.status; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(boolean status) { this.status = status; }

    public interface Register{}
    public interface Modify{}
    public interface ChangeStatus{}
}
