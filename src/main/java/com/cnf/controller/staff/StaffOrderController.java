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

    @GetMapping("/myOrder.do")
    @ResponseBody
    public Result<Page<OrderDetails>> myOrder(
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam Long orderCode) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        OrderDetails orderDetail = new OrderDetails();
        Orders order = new Orders();
        order.setId(orderCode);
        orderDetail.setOrders(order);

        Page<OrderDetails> resultPage = orderDetailService.findPage(pageable, orderDetail);
//        List<OrderDetailDTO> orderDetailsDTOs = resultPage.getContent().stream()
//                .map(OrderMapper::toOrderDetailDTO)
//                .collect(Collectors.toList());
        // Convert to a format that layui expects
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200); // status code for layui
        result.put("msg", "");   // message (optional)
        result.put("count", resultPage.getTotalElements()); // total number of records
        result.put("data", resultPage.getContent()); // paginated data

        return ResultUtil.success(result);
    }

    @GetMapping("/myOrder")
    public String viewOrderDetail(@RequestParam("orderCode") Long orderCode, Model model){
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
