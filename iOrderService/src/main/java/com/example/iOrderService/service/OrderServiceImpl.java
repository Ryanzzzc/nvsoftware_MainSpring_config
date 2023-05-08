package com.example.iOrderService.service;

import com.example.iOrderService.entity.OrderEntity;
import com.example.iOrderService.external.client.PaymentServiceFeignClient;
import com.example.iOrderService.external.client.ProductServiceFeignClient;
import com.example.iOrderService.model.OrderRequest;
import com.example.iOrderService.model.PaymentRequest;
import com.example.iOrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductServiceFeignClient productServiceFeignClient;
    private PaymentServiceFeignClient paymentServiceFeignClient;
    @Override
    public long placeOrder(OrderRequest orderRequest) {//TODO: make this method as transaction
        //call orderService(this) to create order entity with status CREATED, save to orderdb

        log.info("OrderService placeOrder start");
        OrderEntity orderEntity = OrderEntity.builder()
                .productId(orderRequest.getProductId())
                .orderQuantity(orderRequest.getOrderQuantity())
                .totalAmount(orderRequest.getTotalAmount())
                .orderDate(Instant.now())
                .orderStatus("CREATED")
                .paymentMode(orderRequest.getPaymentMode().toString())
                .build();
        orderEntity = orderRepository.save(orderEntity);
        log.info("OrderService placeOrder save to orderdb done");


        //call productService to check quantity and reduceQuantity if Ok

        log.info("ProductServiceFeignClient reduceQuantity start");
        productServiceFeignClient.reduceQuantity(orderRequest.getProductId(),orderRequest.getOrderQuantity());
        log.info("ProductServiceFeignClient reduceQuantity done");


        //call paymentService to charge paymentMode,mark order COMPLETED if success, or else mark CANCELLED

        log.info("PaymentServiceFeignClient doPayment start");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(orderEntity.getOrderId())
                .paymentMode(orderRequest.getPaymentMode())
                .totalAmount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;
        try{
            paymentServiceFeignClient.doPayment(paymentRequest);
            orderStatus = "PLACED";
        }catch (Exception e){
            orderStatus = "PAYMENT FAILED";
        }
        orderEntity.setOrderStatus(orderStatus);
        orderRepository.save(orderEntity);
        log.info("PaymentServiceFeignClient doPayment done");
        return orderEntity.getOrderId();

    }
}
