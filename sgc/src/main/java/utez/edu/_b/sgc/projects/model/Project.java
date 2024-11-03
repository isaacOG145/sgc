package utez.edu._b.sgc.projects.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.projectCat.model.ProjectCategory;

import java.util.List;

@Entity
@Table (name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_project")
    private int id_project;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column (name = "abbreviation", columnDefinition = "VARCHAR(10)")
    private String abbreviation;

    @Column (name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JsonIgnore
    private Customer customer;

    @OneToOne
    private ProjectCategory project_category;

    @Column(name = "status",columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    public Project() {}

    public Project(String name, String abbreviation, String description, Customer customer, ProjectCategory project_category, boolean status) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.description = description;
        this.customer = customer;
        this.project_category = project_category;
        this.status = status;
    }

    public Project(int id_project, String name, String abbreviation, String description, Customer customer, ProjectCategory project_category, boolean status) {
        this.id_project = id_project;
        this.name = name;
        this.abbreviation = abbreviation;
        this.description = description;
        this.status = status;
        this.customer = customer;
        this.project_category = project_category;
    }


    public int getId_project() {
        return id_project;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getDescription() {
        return description;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ProjectCategory getProjectCategory() {
        return project_category;
    }

    public boolean isStatus() {
        return status;
    }

    // Setters
    public void setId_project(int id_project) {
        this.id_project = id_project;
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
        this.project_category = projectCategory;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
