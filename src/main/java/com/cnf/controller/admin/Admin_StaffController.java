package com.cnf.controller.admin;

import com.cnf.entity.Product;
import com.cnf.entity.User;
import com.cnf.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("admin/staff_user")
public class Admin_StaffController {
    @Autowired
    private UserService userService;
    @GetMapping()
    public String index(Model model){
        return findPaginated(1, model);
    }
    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model) {
        int pageSize = 5;
        Page<User> page = userService.findPaginatedStaff(pageNo, pageSize);
        List<User> listStaff = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("users", listStaff);
        return "admin/staff_user/index";
    }
    @GetMapping("/add")
    public String addNew(Model model){
        model.addAttribute("staff", new User());
        return "admin/staff_user/add";
    }
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("staff") User staff,
                      BindingResult bindingResult, Model model
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                model.addAttribute(error.getField() + "_error",
                        error.getDefaultMessage());
            }
            return "admin/satff_user/add";
        }
        userService.saveStaff(staff);
        return "redirect:/admin/staff_user";
    }
}
