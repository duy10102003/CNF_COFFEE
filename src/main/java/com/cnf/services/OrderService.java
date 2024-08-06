package com.cnf.services;


import com.cnf.entity.OrderDetails;
import com.cnf.entity.Orders;
import com.cnf.entity.Product;
import com.cnf.enums.ResultEnum;
import com.cnf.exception.CustomException;
import com.cnf.repository.IOderDetailsRepository;
import com.cnf.repository.IOrderRepository;
import com.cnf.repository.IProductRepository;
import com.cnf.repository.IStatusOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IStatusOrderRepository statusOrderRepository;
    @Autowired
    private IOderDetailsRepository orderDetailsRepository;
    @Autowired
    private IProductRepository productRepository;


    public List<Orders> getAllOrders(){
        return orderRepository.findAll();
    }
    public List<Orders> getAllOrdersByUserId(Long userId){
        return orderRepository.findAllOrderByUserId(userId);
    }
    public Orders getOrderById(Long id){
        return orderRepository.findById(id).orElseThrow();
    }
    public void cancelOrder(Long id){
       Orders existingOrder = orderRepository.findById(id).orElseThrow();
       existingOrder.setStatus_order(statusOrderRepository.findById(0L).orElseThrow());
       orderRepository.save(existingOrder);
    }
    public void updateStatusOrder(Long id){
        Orders existingOrder = orderRepository.findById(id).orElseThrow();
        Long idStatus = existingOrder.getStatus_order().getId();
        idStatus++;
        existingOrder.getStatus_order().setId(idStatus);
        orderRepository.save(existingOrder);
    }
    public Page<Orders> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.orderRepository.findAll(pageable);
    }
    public double getTotalSales(){
        double sum = 0;
        for (Orders temp : orderRepository.findAll()){
            if(temp.getStatus_order().getId() == 3){
                sum = sum + temp.getTotal_money();
            }
        }
        return  sum;
    }
    public int getOrderDelivery(){
        int sum = 0;
        for (Orders temp : orderRepository.findAll()){
            if(temp.getStatus_order().getId() == 3){
                sum++;
            }
        }
        return sum;
    }

    public Long addOrder(Orders order) {
        LocalDateTime now = LocalDateTime.now();

        order.setDate_purchase(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
        //orderIdorder
        orderRepository.save(order);
        //orderId
        Orders order1 = new Orders();
        order1.setId(order.getId());
        //
        for (int i = 0; i < order.getOrderDetails().size(); i++) {
            order.getOrderDetails().get(i).setOrders(order1);
        }

       List<OrderDetails> list =  orderDetailsRepository.saveAll(order.getOrderDetails());
        //,
        for (OrderDetails detail:order.getOrderDetails()){
            updateStoreAndSold(detail.getProduct().getId(),detail.getQuantity());
        }
        if(list==null){
            throw new CustomException(ResultEnum.ADD_ORDER_FAIL);
        }
        return order.getId();
    }

    public void updateStoreAndSold(Long goodsId,int count){
        Product oldGoods = productRepository.findByProId(goodsId);
        Product newGoods = new Product();
        newGoods.setId(oldGoods.getId());
        newGoods.setQuantity(oldGoods.getQuantity()-count);
       // newGoods.setSoldCount(oldGoods.getSoldCount()+count);
//        //0，
//        if(oldGoods.getStoreCount()==count){
//            newGoods.setSoldState(1);
//        }
        productRepository.save(newGoods);
    }
}
