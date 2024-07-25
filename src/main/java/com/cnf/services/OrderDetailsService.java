package com.cnf.services;

import com.cnf.entity.OrderDetails;
import com.cnf.entity.Product;
import com.cnf.repository.IOderDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailsService {
    @Autowired
    IOderDetailsRepository oderDetailsRepository;
    public List<OrderDetails> getAllOrderDetailsByOrderId(Long id){
        return oderDetailsRepository.findAllOrderDetailsByOrderId(id);
    }
    public List<Product> getTop5ProductsByRevenue() {
        Pageable pageable = PageRequest.of(0, 5);
        return oderDetailsRepository.findTop5ProductsByRevenue(pageable);
    }
}
