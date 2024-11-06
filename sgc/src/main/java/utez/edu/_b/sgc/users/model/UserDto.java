package utez.edu._b.sgc.users.model;

import jakarta.validation.constraints.*;
import utez.edu._b.sgc.customer.model.CustomerDto;
import utez.edu._b.sgc.role.model.Role;

import java.util.HashSet;
import java.util.Set;

public class UserDto {

    @NotNull(groups = {Modify.class,ChangeStatus.class},message = "El id no puede ser nulo")
    private Long id;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo nombre no puede estar vacio")
    @Pattern(groups = {Modify.class, Register.class},regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El nombre no puede contener caracteres especiales")
    private String name;

    @NotBlank(groups = {Modify.class,Register.class}, message = "El campo apellido no puede estar vacio")
    @Pattern(groups = {Modify.class, Register.class},regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El apellido no puede contener caracteres especiales")
    private String lastName;

    @NotBlank(groups = {Modify.class, Register.class},message = "El correo electrónico es obligatorio")
    @Email(groups = {Modify.class, Register.class},message = "El correo electrónico debe ser válido")
    private String email;

    @NotBlank(groups = {Modify.class, Register.class},message = "El teléfono es obligatorio")
    @Pattern(groups = {Modify.class, Register.class},regexp = "^[0-9]+$", message = "El teléfono debe contener solo números")
    @Size(groups = {Modify.class, Register.class}, min = 10, max = 10, message = "El teléfono debe tener 10 dígitos")
    private String phoneNumber;

    @NotBlank(groups = {Modify.class, Register.class},message = "La contraseña es obligatoria")
    @Size(groups = {Modify.class, Register.class},min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotNull(groups = {Modify.class,ChangeStatus.class},message = "El rol no puede ser nulo")
    private Set<Role> roleIds = new HashSet<>();

    public UserDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Role> roleIds) {
        this.roleIds = roleIds;
    }

    public interface Register {
    }

    public interface Modify {
    }

    public interface ChangeStatus {
    }

}