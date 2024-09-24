package mk.ukim.finki.ib.documentvalidator.repository;

import mk.ukim.finki.ib.documentvalidator.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("confirmationTokenRepository")
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Long> {
    ConfirmationToken findConfirmationTokenByConfirmationToken(String confirmationToken);
}
