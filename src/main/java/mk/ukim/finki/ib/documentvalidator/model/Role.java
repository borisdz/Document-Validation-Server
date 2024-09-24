package mk.ukim.finki.ib.documentvalidator.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_STANDARD, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
