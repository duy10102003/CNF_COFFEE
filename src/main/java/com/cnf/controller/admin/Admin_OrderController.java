package com.cnf.controller.admin;

import com.cnf.entity.Orders;
import com.cnf.services.OrderDetailsService;
import com.cnf.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/order")
public class Admin_OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailsService orderDetailsService;

    @GetMapping("")
    public String index(Model model){

        return findPaginated(1, model);
    }
    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable (value = "pageNo") int pageNo, Model model) {
        int pageSize = 5;
        Page<Orders> page = orderService.findPaginated(pageNo, pageSize);
        List<Orders> listOrder = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("orders", listOrder);
        return "admin/order/index";
    }
    @GetMapping("/confirm_order/{id}")
    public String confirmOrder(@PathVariable("id") Long id, Model model){
        orderService.updateStatusOrder(id);
        return "redirect:/admin/order";
    }
    @GetMapping("/confirm_shipped/{id}")
    public String confirmShipped(@PathVariable("id") Long id, Model model){
        orderService.updateStatusOrder(id);
        return "redirect:/admin/order";
    }
    @GetMapping("/details/{id}")
    public String viewDetails(@PathVariable("id") Long id, Model model){
        model.addAttribute("order_details", orderDetailsService.getAllOrderDetailsByOrderId(id));
        return "admin/order/details";
    }
}
