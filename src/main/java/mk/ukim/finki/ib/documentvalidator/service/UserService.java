package mk.ukim.finki.ib.documentvalidator.service;

import mk.ukim.finki.ib.documentvalidator.model.Role;
import mk.ukim.finki.ib.documentvalidator.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    User login (String username, String password);

    String getUserSalt(String userSalt);

    ResponseEntity<?> register(String userEmail, String username, String password, String repeatPassword, Role role);

    UserDetails loadUserByUsername(String s) throws UsernameNotFoundException;

    ResponseEntity<?> confirmEmail(String confirmationToken);

}
