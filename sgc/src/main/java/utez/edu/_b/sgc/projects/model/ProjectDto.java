package utez.edu._b.sgc.projects.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.projectCat.model.ProjectCategory;

public class ProjectDto {

    @NotNull(groups = {Modify.class, ChangeStatus.class}, message = "El id no puede ser nulo")
    private Long id;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo nombre no puede estar vacío")
    private String name;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo abreviación no puede estar vacío")
    private String abbreviation;

    @NotBlank(groups = {Modify.class, Register.class}, message = "El campo descripción no puede estar vacío")
    private String description;

    @NotNull(groups = {Register.class, Modify.class}, message = "El cliente no puede ser nulo")
    private Long customerId;

    @NotNull(groups = {Register.class, Modify.class}, message = "La categoría del proyecto no puede ser nula")
    private Long projectCategoryId;

    private boolean status;

    // Getters y setters
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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProjectCategoryId() {
        return projectCategoryId;
    }

    public void setProjectCategoryId(Long projectCategoryId) {
        this.projectCategoryId = projectCategoryId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public interface Register {}
    public interface Modify {}
    public interface ChangeStatus {}
}