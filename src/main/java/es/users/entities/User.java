package es.users.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size; // Añadido para longitud

@Entity
@Table(name = "users", indexes = { @Index(name = "idx_nif", columnList = "nif"),
        @Index(name = "idx_email", columnList = "email") })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long   id;
    @NotBlank(message = "NIF cannot be empty")
    @Size(max = 9, message = "NIF cannot exceed 9 characters")
    @Column(name = "nif", unique = true, nullable = false, length = 9)
    private String nif;
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    @Column(name = "nombre", nullable = false, length = 50)
    private String name;
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be a valid format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    public User() {
        super();
    }


    public User(String nif, String name, String email) {
        this.nif = nif;
        this.name = name;
        this.email = email;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getNif() {
        return nif;
    }


    public void setNif(String nif) {
        this.nif = nif;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User user))
            return false;
        if (this.id == null || user.id == null) {
            return false;
        }
        return Objects.equals(id, user.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public String toString() {
        return "User{" + "id=" + id + ", nif='" + nif + '\'' + ", name='" + name + '\'' + ", email='" + email + '\''
                + '}';
    }
}