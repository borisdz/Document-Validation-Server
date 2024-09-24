package mk.ukim.finki.ib.documentvalidator.config;

import mk.ukim.finki.ib.documentvalidator.encryption.PassEncryption;
import mk.ukim.finki.ib.documentvalidator.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CustomUsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    private final PassEncryption passEncryption;
    private final UserService userService;

    public CustomUsernamePasswordAuthenticationProvider( PassEncryption passEncryption, UserService userService) {
        this.passEncryption = passEncryption;
        this.userService = userService;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if("".equals(username)||"".equals(password))
            throw new BadCredentialsException("Invalid Credentials");

        UserDetails userDetails = this.userService.loadUserByUsername(username);
        String salt = this.userService.getUserSalt(username);
        if(!passEncryption.verifyUserPassword(password,userDetails.getPassword(),salt))
            throw new BadCredentialsException("Password is incorrect");
        return new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}