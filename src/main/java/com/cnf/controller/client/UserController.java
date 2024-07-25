package com.cnf.controller.client;

import com.cnf.entity.User;
import com.cnf.services.CartService;
import com.cnf.services.OrderDetailsService;
import com.cnf.services.OrderService;
import com.cnf.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
}
