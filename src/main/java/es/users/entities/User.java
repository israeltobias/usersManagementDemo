package es.users.entities;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;
    @NotBlank(message = "NIF cannot be empty")
    private String nif;
    @NotBlank(message = "nombre cannot be empty")
    private String name;
    @Email(message = "Email must be a valid format")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    public User() {
        super();
    }


    public User(@NotBlank(message = "NIF cannot be empty") String nif,
            @NotBlank(message = "Nombre cannot be empty") String name,
            @Email(message = "Email must be a valid format") @NotBlank(message = "NIF cannot be empty") @Email String email) {
        super();
        this.nif = nif;
        this.name = name;
        this.email = email;
    }


    @Override
    public String toString() {
        return "User [id=" + id + ", nif=" + nif + ", name=" + name + ", email=" + email + "]";
    }


    @Override
    public int hashCode() {
        return Objects.hash(email, id, name, nif);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return Objects.equals(email, other.email) && Objects.equals(id, other.id) && Objects.equals(name, other.name)
                && Objects.equals(nif, other.nif);
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
}
