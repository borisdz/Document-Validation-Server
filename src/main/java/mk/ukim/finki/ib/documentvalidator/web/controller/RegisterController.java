package mk.ukim.finki.ib.documentvalidator.web.controller;

import mk.ukim.finki.ib.documentvalidator.model.Role;
import mk.ukim.finki.ib.documentvalidator.model.exceptions.InvalidArgumentsException;
import mk.ukim.finki.ib.documentvalidator.model.exceptions.PasswordsDoNotMatchException;
import mk.ukim.finki.ib.documentvalidator.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getRegisterPage(@RequestParam(required = false) String error, Model model) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        return "register";
    }

    @PostMapping
    public String register(
            @RequestParam String userEmail,
            @RequestParam String userName,
            @RequestParam String userPassword,
            @RequestParam String repeatedPassword) {
        try {
            this.userService.register(userEmail, userName, userPassword, repeatedPassword, Role.ROLE_STANDARD);
            return "redirect:/login";
        } catch (InvalidArgumentsException | PasswordsDoNotMatchException exception) {
            return "redirect:/register?error" + exception.getMessage();
        }
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token")String confirmationToken){
        return userService.confirmEmail(confirmationToken);
    }
}