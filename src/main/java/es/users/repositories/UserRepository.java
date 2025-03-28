package es.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.users.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

}
