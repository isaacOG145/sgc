package utez.edu._b.sgc.users.model;
import jakarta.persistence.*;
import utez.edu._b.sgc.role.model.Role;

import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "last_name", columnDefinition = "VARCHAR(100)")
    private String lastName;

    @Column(name = "email", columnDefinition = "VARCHAR(100)")
    private String email;

    @Column(name ="phone_number", columnDefinition = "VARCHAR(10)")
    private String phoneNumber;

    @Column(name = "password", columnDefinition = "VARCHAR(255)")
    private String password;

    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    @Column(name = "code", columnDefinition = "VARCHAR(10)")
    private String code;

    @Column(name = "verified", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean isVerified = false;

    @Column(name = "create_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    public User() {}

    public User(Long id, String email, String code) {
        this.id = id;
        this.email = email;
        this.code = code;
    }

    public User(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public User(String name, String lastName, String email, String phoneNumber, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public User(String name, String lastName, String email, String phoneNumber, String password, Role role, boolean status, boolean verified) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.status = status;
        this.isVerified = verified;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public boolean isStatus() {
        return status;
    }

    public String getCode() { return code; }

    public boolean isVerified() {
        return isVerified;
    }

    public Role getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setCode(String code) { this.code = code; }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }
}



