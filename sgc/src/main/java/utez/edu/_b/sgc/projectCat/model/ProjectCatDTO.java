package utez.edu._b.sgc.projectCat.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ProjectCatDTO {


    @NotNull(groups = {Modify.class,ChangeStatus.class},message = "El id no puede ser nulo")
    private Long id;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo nombre no puede estar vacio")
    @Pattern(groups = {Modify.class, Register.class},regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El nombre no puede contener caracteres especiales")
    private String name;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo descripcion no puede estar vacio")
    @Pattern(groups = {Modify.class, Register.class}, regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "La descripcion no puede contener caracteres especiales")
    private String description;


    public ProjectCatDTO() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public interface Register{}
    public interface Modify{}
    public interface ChangeStatus{}
}