package utez.edu._b.sgc.users.model;

import jakarta.validation.constraints.*;
import utez.edu._b.sgc.role.model.Role;

public class UserDto {

    @NotNull(groups = {Modify.class, ChangeStatus.class}, message = "El id no puede ser nulo")
    private Long id;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo nombre no puede estar vacío")
    private String name;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo apellido no puede estar vacío")
    private String lastName;

    @NotBlank(groups = {ChangePassword.class,Modify.class, Register.class, FindByEmail.class,VerifyCode.class}, message = "El correo electrónico es obligatorio")
    private String email;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El teléfono es obligatorio")
    private String phoneNumber;

    @NotBlank(groups = {ChangePassword.class,Register.class}, message = "La contraseña es obligatoria")
    private String password;

    @NotNull(groups = {Modify.class, Register.class}, message = "El rol no puede ser nulo")
    private Role role;

    @NotBlank(groups = {VerifyCode.class})
    private String code;

    private boolean isVerified;

    private boolean status;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isVerified() { return this.isVerified; }

    public void setVerified(boolean isVerified) { this.isVerified = isVerified; }

    public interface Register {
    }

    public interface Modify {
    }

    public interface ChangeStatus {
    }

    public interface FindByEmail {

    }
    public interface VerifyCode {

    }

    public interface ChangePassword {

    }
}
