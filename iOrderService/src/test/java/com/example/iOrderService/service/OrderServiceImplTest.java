package com.example.iOrderService.service;

import com.example.iOrderService.entity.OrderEntity;
import com.example.iOrderService.external.client.PaymentServiceFeignClient;
import com.example.iOrderService.external.client.ProductServiceFeignClient;
import com.example.iOrderService.model.OrderResponse;
import com.example.iOrderService.model.PaymentMode;
import com.example.iOrderService.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceImplTest {

    @Mock
    RestTemplate restTemplate;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceFeignClient productServiceFeignClient;

    @Mock
    private PaymentServiceFeignClient paymentServiceFeignClient;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();


    @DisplayName("Get order detail - SUCCESS")
    @Test
    void testWhenGetOrderSuccess() {
        //mock part
        OrderEntity orderEntity = getMockOrderEntity();
        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(orderEntity));

        Mockito.when(restTemplate.getForObject(
                "http://PRODUCT-SERVICE/products/"+orderEntity.getProductId(),
                OrderResponse.ProductResponse.class
        )).thenReturn(getProductResponse());

        Mockito.when(restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payments/"+orderEntity.getOrderId(),
                OrderResponse.PaymentResponse.class
        )).thenReturn(getPaymentResponse());

        //actual call
        OrderResponse orderResponse = orderService.getOrderById(4);


        // verify call
        Mockito.verify(orderRepository,Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(restTemplate,Mockito.times(1)).getForObject(
                "http://PRODUCT-SERVICE/products/"+orderEntity.getProductId(),
                OrderResponse.ProductResponse.class
        );

        Mockito.verify(restTemplate,Mockito.times(1)).getForObject(
                "http://PAYMENT-SERVICE/payments/"+orderEntity.getOrderId(),
                OrderResponse.PaymentResponse.class
        );

        //assert response
        Assertions.assertNotNull(orderResponse);
        Assertions.assertEquals(orderEntity.getOrderId(),orderResponse.getOrderId());

    }

    private OrderEntity getMockOrderEntity() {
        return OrderEntity.builder()
                .orderId(4)
                .productId(1)
                .orderQuantity(1)
                .totalAmount(1299)
                .orderDate(Instant.now())
                .orderStatus("PLACED")
                .build();
    }

    private OrderResponse.PaymentResponse getPaymentResponse() {
        return OrderResponse.PaymentResponse.builder()
                .orderId(4)
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .paymentStatus("SUCCESS")
                .totalAmount(1299)
                .build();
    }

    private OrderResponse.ProductResponse getProductResponse() {
        return OrderResponse.ProductResponse.builder()
                .productId(1)
                .productName("mini")
                .productPrice(1299)
                .productQuantity(1)
                .build();
    }
}