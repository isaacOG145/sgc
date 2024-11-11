package utez.edu._b.sgc.projectCat.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProjectCatDTO {


    @NotNull(groups = {Modify.class,ChangeStatus.class},message = "El id no puede ser nulo")
    private Long id;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo nombre no puede estar vacio")
    private String name;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo descripcion no puede estar vacio")
    private String description;

    private boolean status;


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

    public boolean isStatus() { return this.status; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(boolean status) { this.status = status; }

    public interface Register{}
    public interface Modify{}
    public interface ChangeStatus{}
}