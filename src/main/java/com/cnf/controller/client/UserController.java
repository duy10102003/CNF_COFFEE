package com.cnf.controller.client;

import com.cnf.dto.request.Utility;
import com.cnf.dto.respone.GenericResponse;
import com.cnf.entity.PasswordResetToken;
import com.cnf.entity.User;
import com.cnf.exception.UserNotFoundException;
import com.cnf.services.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailsService orderDetailsService;
    @Autowired
    private PasswordResetTokenService verificationTokenService;
    @Autowired
    private EmailSenderService emailSenderService;

    @GetMapping("/login")
    public String login(HttpSession session) {
        session.setAttribute("totalItems", cartService.getSumQuantity(session));
        session.setAttribute("cart", cartService.getCart(session));
        return "client/user/login";
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "client/user/register";
    }
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                model.addAttribute(error.getField() + "_error",
                        error.getDefaultMessage());
            }
            return "client/user/register";
        }
        userService.save(user);
        return "redirect:/login";
    }
    @GetMapping("/account")
    public String account(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getUserbyUserName(username);
        model.addAttribute("fullName", user.getFull_name());
        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.getAllOrdersByUserId(user.getId()));
        return "client/user/account";
    }
    @PostMapping("/account/edit")
    public String editInfo(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes){
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("message", "Save successfully!");
        return "redirect:/account";
    }
    @GetMapping("order_details/{id}")
    public String viewDetails(@PathVariable("id") Long id, Model model){
        var orderDetails = orderDetailsService.getAllOrderDetailsByOrderId(id);
        double totalPrice = orderDetails.stream().mapToDouble(item -> item.getProduct().getPrice() *
                            item.getQuantity()).sum();
        model.addAttribute("order_details", orderDetails);
        model.addAttribute("total_price", totalPrice);
        return "client/user/details";
    }

    @GetMapping("/forgotpassword")
    public String resetPass() {
        return "client/user/forgotPassword";
    }

    @PostMapping("/forgotpassword")
    public String resetPassword(HttpServletRequest request,
                                         @RequestParam("email") String userEmail, Model model) throws UserNotFoundException {

        Optional<User> optionalUser = userService.findByEmail(userEmail);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

        if (user == null) {
            throw new UserNotFoundException("Email not exist");
        }
        try {
            PasswordResetToken token = emailSenderService.createVerificationToken(user);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token.getToken();
            emailSenderService.sendEmailForgetPassword(userEmail, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error while sending email");
        }

        return "client/user/forgotPassword";
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = verificationTokenService.findByToken(token);
        PasswordResetToken tokenRequest = tokenOpt.orElseThrow(() -> new RuntimeException("Token Not Found"));

        User user = userService.getUserByUserID(tokenRequest.getId());

        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "client/user/message";
        }

        return "client/user/resetPassword";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        Optional<PasswordResetToken> tokenOpt = verificationTokenService.findByToken(token);
        PasswordResetToken tokenRequest = tokenOpt.orElseThrow(() -> new RuntimeException("Token Not Found"));

        User user = userService.getUserByUserID(tokenRequest.getUser().getId());

        model.addAttribute("title", "Reset your password");

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "client/user/message";
        } else {
            userService.updatePassword(user, password);

            model.addAttribute("message", "You have successfully changed your password.");
        }

        return "client/user/message";
    }


}
