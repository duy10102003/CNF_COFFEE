package com.cnf.controller.staff;

import com.cnf.daos.OrderDetailDTO;
import com.cnf.daos.OrderMapper;
import com.cnf.entity.OrderDetails;
import com.cnf.entity.Orders;
import com.cnf.entity.Result;
import com.cnf.services.OrderDetailsService;
import com.cnf.services.OrderService;
import com.cnf.ultils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/staff/order")
public class StaffOrderController{
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailsService orderDetailService;

    @PostMapping("/addorder")
    @ResponseBody
    private Result addOrder(@RequestBody Orders order ){


        Long orderCode = orderService.addOrder(order);

        return ResultUtil.success(orderCode);
    }

//    @GetMapping("/myOrder.do")
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> myOrder(
//            @RequestParam int pageNum,
//            @RequestParam int pageSize,
//            @RequestParam Long orderCode) {
//        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
//        Page<OrderDetails> resultPage = orderDetailService.getOrderDetailsByOrderId(orderCode, pageNum, pageSize);
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("code", 200);
//        result.put("msg", "");
//        result.put("count", resultPage.getTotalElements());
//        result.put("data", resultPage.getContent());
//
//        return ResponseEntity.ok(result);
//        }
//
//
//    @GetMapping("/myOrder")
//    public String viewOrderDetail(@RequestParam("orderCode") Long orderCode, Model model){
//        model.addAttribute("orderCode",orderCode);
//        return "staff/myOrder";
//    }

    // Phương thức trả về JSON cho bảng order
    @GetMapping("/myOrder.do")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> myOrder(
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam Long orderCode) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<OrderDetails> resultPage = orderDetailService.getOrderDetailsByOrderId(orderCode, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "");
        result.put("count", resultPage.getTotalElements());
        result.put("data", resultPage.getContent());

        return ResponseEntity.ok(result);
    }

    // Phương thức để trả về trang hiển thị chi tiết đơn hàng
    @GetMapping("/myOrder")
    public String viewOrderDetail(@RequestParam("orderCode") Long orderCode, Model model) {
        model.addAttribute("orderCode", orderCode);
        return "staff/myOrder"; // Tên template Thymeleaf
    }

    @PostMapping("/delProduct")
    @ResponseBody
    public Result<OrderDetails> deleteByOrderDetailIds(@RequestParam("ids") String ids){
        orderDetailService.deleteByIds(ids);
        return ResultUtil.success();
    }
}
