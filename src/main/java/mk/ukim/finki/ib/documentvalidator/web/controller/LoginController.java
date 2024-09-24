package mk.ukim.finki.ib.documentvalidator.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import mk.ukim.finki.ib.documentvalidator.model.User;
import mk.ukim.finki.ib.documentvalidator.model.exceptions.InvalidUserCredentialsException;
import mk.ukim.finki.ib.documentvalidator.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getLoginPage(){
        return "login";
    }

    @PostMapping
    public String login(HttpServletRequest req, Model model){
        User user = null;
        try{
            user = this.userService.login(req.getParameter("userName"),req.getParameter("userPassword"));
            req.getSession().setAttribute("user",user);
            return "redirect:/document";
        }catch (InvalidUserCredentialsException e){
            model.addAttribute("hasError", true);
            model.addAttribute("error",e.getMessage());
            return "login";
        }
    }
}
