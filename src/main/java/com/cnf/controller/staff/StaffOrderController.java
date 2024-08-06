package com.cnf.controller.staff;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/staff/order")
public class StaffOrderController{
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailsService orderDetailService;
    /**
     * 
     * @return
     */
    @PostMapping("/addorder")
    @ResponseBody
    private Result addOrder(@RequestBody Orders order ){



        Long orderCode = orderService.addOrder(order);

        return ResultUtil.success(orderCode);
    }

    @GetMapping("/myOrder.do")
    @ResponseBody
    public Result<Page<OrderDetails>> myOrder(
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam Long orderId) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        OrderDetails orderDetail = new OrderDetails();
        Orders order = new Orders();
        order.setId(orderId);
        orderDetail.setOrders(order);

        Page<OrderDetails> resultPage = orderDetailService.findPage(pageable, orderDetail);

        // Convert to a format that layui expects
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200); // status code for layui
        result.put("msg", "");   // message (optional)
        result.put("count", resultPage.getTotalElements()); // total number of records
        result.put("data", resultPage.getContent()); // paginated data

        return ResultUtil.success(result);
    }

    @GetMapping("/myOrder")
    public String viewOrderDetail(@RequestParam("orderCode") int orderCode, Model model){
        model.addAttribute("orderCode",orderCode);
        return "/staff/myOrder";
    }

    @PostMapping("/delProduct")
    @ResponseBody
    public Result<OrderDetails> deleteByOrderDetailIds(@RequestParam("ids") String ids){
        orderDetailService.deleteByIds(ids);
        return ResultUtil.success();
    }
}
