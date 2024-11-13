package utez.edu._b.sgc.projectCat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import utez.edu._b.sgc.projects.model.Project;

import java.util.List;

@Entity
@Table (name = "project_category")
public class ProjectCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id_project_category")
    private Long idProjectCategory;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column (name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status",columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;


    @OneToMany(mappedBy = "project_category")
    @JsonIgnore
    private List<Project> project;

    public ProjectCategory() {
    }

    public ProjectCategory(Long id) {
        this.idProjectCategory = id;
    }

    public ProjectCategory(String name, String description, boolean status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public ProjectCategory(Long idProjectCategory, String name, String description, boolean status) {
        this.idProjectCategory = idProjectCategory;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public long getId() {
        return idProjectCategory;
    }

    public void setId(Long id) {
        this.idProjectCategory = idProjectCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


}
