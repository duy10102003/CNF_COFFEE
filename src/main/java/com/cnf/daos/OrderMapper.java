package com.cnf.daos;

import com.cnf.entity.OrderDetails;
import com.cnf.entity.Orders;
import com.cnf.entity.Product;

import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDTO1 toOrderDTO(Orders order) {
        OrderDTO1 orderDTO = new OrderDTO1();
        orderDTO.setId(order.getId());
        orderDTO.setStatus_id(order.getStatus_order().getId());
        orderDTO.setTotal_money(order.getTotal_money());
        orderDTO.setOrderDetails(order.getOrderDetails().stream()
                .map(OrderMapper::toOrderDetailDTO)
                .collect(Collectors.toList()));
        return orderDTO;
    }

    public static OrderDetailDTO toOrderDetailDTO(OrderDetails orderDetail) {
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        orderDetailDTO.setId(orderDetail.getId());
        orderDetailDTO.setQuantity(orderDetail.getQuantity());
        orderDetailDTO.setDel(orderDetail.getDel());
        orderDetailDTO.setTotal_money(orderDetail.getTotal_money());
        orderDetailDTO.setStatus(orderDetail.getStatus());
        orderDetailDTO.setProduct(toProductDTO(orderDetail.getProduct()));
        return orderDetailDTO;
    }

    public static ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setImage(product.getImg());
        return productDTO;
    }


}
