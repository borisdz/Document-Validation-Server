package mk.ukim.finki.ib.documentvalidator.repository;

import mk.ukim.finki.ib.documentvalidator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUserEmailIgnoreCase(String emailId);
    Optional<User> findUserByUserEmail(String email);
    Optional<User> findUserByUserNameAndUserPassword(String username, String password);
    Optional<User> findUserByUserName(String username);
    Boolean existsUserByUserEmail(String email);


}
