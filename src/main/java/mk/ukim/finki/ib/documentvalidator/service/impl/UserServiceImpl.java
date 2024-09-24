package mk.ukim.finki.ib.documentvalidator.service.impl;

import mk.ukim.finki.ib.documentvalidator.encryption.PassEncryption;
import mk.ukim.finki.ib.documentvalidator.model.ConfirmationToken;
import mk.ukim.finki.ib.documentvalidator.model.Role;
import mk.ukim.finki.ib.documentvalidator.model.User;
import mk.ukim.finki.ib.documentvalidator.model.exceptions.*;
import mk.ukim.finki.ib.documentvalidator.repository.ConfirmationTokenRepository;
import mk.ukim.finki.ib.documentvalidator.repository.UserRepository;
import mk.ukim.finki.ib.documentvalidator.service.EmailService;
import mk.ukim.finki.ib.documentvalidator.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, ConfirmationTokenRepository confirmationTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        String secPassword = PassEncryption.generateSecurePassword(password,userRepository.findUserByUserName(username).get().getSaltValue());

        return userRepository.findUserByUserNameAndUserPassword(username,secPassword).orElseThrow(InvalidUserCredentialsException::new);
    }

    @Override
    public String getUserSalt(String username) {
        return userRepository.findUserByUserName(username).get().getSaltValue();
    }

    @Override
    public ResponseEntity<?> register(String userEmail, String username, String password, String repeatPassword, Role role) {
        if (userEmail == null || username == null || password == null || username.isEmpty() || password.isEmpty())
            throw new InvalidUsernameOrPasswordException();

        if (!password.equals(repeatPassword))
            throw new PasswordsDoNotMatchException();

        if(this.userRepository.findUserByUserEmailIgnoreCase(userEmail)!=null)
            throw new EmailAlreadyExistsException(userEmail);

        if (this.userRepository.findUserByUserName(username).isPresent())
            throw new UsernameAlreadyExistsException(username);

        String saltValue = PassEncryption.genSaltValue(30);
        User user = new User(userEmail, username, "", role);
        user.setUserPassword(PassEncryption.generateSecurePassword(password, saltValue));
        user.setSaltValue(saltValue);

        userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getUserEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText(("To confirm your account, please click here : "
                +"https://localhost:8443/register/confirm-account?token="+confirmationToken.getConfirmationToken()));
        System.out.println("Confirmation Token: " + confirmationToken.getConfirmationToken());
        emailService.sendEmail(mailMessage);
        userRepository.save(user);

        return ResponseEntity.ok("Verify email by the link sent on your email address.");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        return userRepository.findUserByUserName(username).orElseThrow(()->new UsernameNotFoundException(username));

    }

    @Override
    public ResponseEntity<?> confirmEmail(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findConfirmationTokenByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findUserByUserEmailIgnoreCase(token.getUser().getUserEmail());
            user.setEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok("Email verified successfully!");
        }

        return ResponseEntity.badRequest().body("Error: Couldn't verify email");
    }
}
