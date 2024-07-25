package com.cnf.services;


import com.cnf.entity.Orders;
import com.cnf.repository.IOrderRepository;
import com.cnf.repository.IStatusOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IStatusOrderRepository statusOrderRepository;
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
}
