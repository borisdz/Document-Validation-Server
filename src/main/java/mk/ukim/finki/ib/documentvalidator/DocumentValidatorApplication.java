package mk.ukim.finki.ib.documentvalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
public class DocumentValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentValidatorApplication.class, args);
	}

}
