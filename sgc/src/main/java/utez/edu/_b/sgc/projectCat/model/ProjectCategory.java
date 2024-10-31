package utez.edu._b.sgc.projectCat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import utez.edu._b.sgc.projects.model.Project;

@Entity
@Table (name = "project_category")
public class ProjectCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id_project_category")
    private int idProjectCategory;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column (name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status",columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;


    @OneToOne(mappedBy = "project_category")
    @JsonIgnore
    private Project project;


}
