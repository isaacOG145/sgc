package utez.edu._b.sgc.projects.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.customer.model.CustomerDto;
import utez.edu._b.sgc.projectCat.model.ProjectCategory;

public class ProjectDto {

    @NotNull(groups = {Modify.class, ChangeStatus.class},message = "El id no puede ser nulo")
    private Long id;

    @NotBlank(groups = {Modify.class,Register.class}, message = "El campo nombre no puede estar vacio")
    private String name;

    @NotBlank(groups = {Modify.class,Register.class}, message = "El campo abreviación no puede estar vacio")
    private String abbreviation;

    @NotBlank(groups = {Modify.class,Register.class}, message = "El campo descripcion no puede estar vacio")
    private String description;

    @NotNull(groups = {Register.class, Modify.class}, message = "El cliente no puede ser nulo")
    private Customer customer;

    @NotNull(groups = {Register.class, Modify.class}, message = "La categoria del proyecto no puede ser nulo")
    private ProjectCategory projectCategory;

    private boolean status;

    public ProjectDto() {}

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setProjectCategory(ProjectCategory projectCategory) {
        this.projectCategory = projectCategory;
    }

    public void setStatus(boolean status) { this.status = status; }
    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public String getDescription() {
        return this.description;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public ProjectCategory getProjectCategory() {
        return this.projectCategory;
    }

    public boolean isStatus() {
        return status;
    }

    public interface Register{}
    public interface Modify{}
    public interface ChangeStatus{}
}
