package utez.edu._b.sgc.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import utez.edu._b.sgc.projects.model.Project;

import java.util.List;

@Entity
@Table(name ="customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_customer")
    private long id;

    @Column (name = "name", columnDefinition = "VARCHAR(100)")
    private String name;

    @Column (name = "email", columnDefinition = "VARCHAR(100)")
    private String email;

    @Column (name = "phone_number", columnDefinition = "VARCHAR (10)")
    private String phoneNumber;

    @Column(name = "status",columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Project> project;

    public Customer() {}

    public Customer(Long idCustomer) {
        this.id = idCustomer;
    }

    public Customer(String name, String email, String phoneNumber, boolean status) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isStatus() {
        return status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
