    package utez.edu._b.sgc.customer.model;

<<<<<<< HEAD
    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Pattern;
=======
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
>>>>>>> 0159cf5c20b826972a3c1c5616e41cba125393bc

    public class CustomerDto {

        @NotNull(groups = {Modify.class,ChangeStatus.class},message = "El id no puede ser nulo")
        private Long id;

<<<<<<< HEAD
        @NotBlank(groups = {Modify.class, Register.class}, message = "El campo nombre no puede estar vacio")
        @Pattern(groups = {Modify.class, Register.class},regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
                message = "El nombre no puede contener caracteres especiales")
        private String name;

        @Email(groups = {Modify.class, Register.class},message = "Debe ser un correo electrónico válido")
        @NotBlank(groups = {Modify.class, Register.class},message = "El correo no puede estar vacío")
        private String email;

        @NotBlank(groups = {Modify.class, Register.class}, message = "El campo teléfono no puede estar vacío")
        @Pattern(groups = {Modify.class, Register.class},regexp = "^[0-9]+$", message = "El teléfono solo puede contener números")
        private String phone;

        public CustomerDto() {}
=======
    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo nombre no puede estar vacio")
    private String name;

    @NotBlank(groups = {Modify.class, Register.class},message = "El correo no puede estar vacío")
    private String email;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo teléfono no puede estar vacío")
    private String phone;

    private boolean status;

    public CustomerDto() {}
>>>>>>> 0159cf5c20b826972a3c1c5616e41cba125393bc

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

        public interface Register{}
        public interface Modify{}
        public interface ChangeStatus{}
    }
<<<<<<< HEAD
=======

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
>>>>>>> 0159cf5c20b826972a3c1c5616e41cba125393bc
