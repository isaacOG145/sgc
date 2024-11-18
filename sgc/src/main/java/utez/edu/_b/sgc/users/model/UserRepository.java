package utez.edu._b.sgc.users.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utez.edu._b.sgc.projectCat.model.ProjectCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    List<User> findByStatus(boolean status);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

}