package com.example.iOrderService.service;

import com.example.iOrderService.entity.OrderEntity;
import com.example.iOrderService.external.client.PaymentServiceFeignClient;
import com.example.iOrderService.external.client.ProductServiceFeignClient;
import com.example.iOrderService.model.OrderRequest;
import com.example.iOrderService.model.OrderResponse;
import com.example.iOrderService.model.PaymentMode;
import com.example.iOrderService.model.PaymentRequest;
import com.example.iOrderService.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(orderEntity));

        when(restTemplate.getForObject(
                "http://PRODUCT-SERVICE/products/"+orderEntity.getProductId(),
                OrderResponse.ProductResponse.class
        )).thenReturn(getProductResponse());

        when(restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payments/"+orderEntity.getOrderId(),
                OrderResponse.PaymentResponse.class
        )).thenReturn(getPaymentResponse());

        //actual call
        OrderResponse orderResponse = orderService.getOrderById(4);


        // verify call
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(
                "http://PRODUCT-SERVICE/products/"+orderEntity.getProductId(),
                OrderResponse.ProductResponse.class
        );

        verify(restTemplate, times(1)).getForObject(
                "http://PAYMENT-SERVICE/payments/"+orderEntity.getOrderId(),
                OrderResponse.PaymentResponse.class
        );

        //assert response
        Assertions.assertNotNull(orderResponse);
        Assertions.assertEquals(orderEntity.getOrderId(),orderResponse.getOrderId());

    }
    @DisplayName("Get OrderDetail OrderId NOT FOUND")
    @Test
    void testWhenOrderIdNotFound() {

        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
        //actual call

        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> orderService.getOrderById(4));
        Assertions.assertEquals("OrderService getOrderById NOT FOUND for: 4",runtimeException.getMessage());

        //verify
        verify(orderRepository, times(1)).findById(anyLong());
    }
    @DisplayName("Place Order - Success")
    @Test
    void testWhenPlaceOrderSuccess() {
        OrderEntity orderEntity = getMockOrderEntity();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(orderEntity);

        when(productServiceFeignClient.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        when(paymentServiceFeignClient.doPayment(Mockito.any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository,times(2)).save(any());
        verify(productServiceFeignClient,times(1)).reduceQuantity(anyLong(),anyLong());
        verify(paymentServiceFeignClient,times(1)).doPayment(any(PaymentRequest.class));

        Assertions.assertEquals(orderEntity.getOrderId(),orderId);

    }

    @DisplayName("Place Order Payment Failed - Failed")
    @Test
    void testWhenPlaceOrderFailed() {
        //mock part
        OrderEntity orderEntity = getMockOrderEntity();
        OrderRequest orderRequest = getMockOrderRequest();
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(orderEntity.getOrderId())
                .totalAmount(orderRequest.getTotalAmount())
                .paymentMode(orderRequest.getPaymentMode())
                .build();

        when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(orderEntity);

        when(productServiceFeignClient.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        when(paymentServiceFeignClient.doPayment(Mockito.any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("PAYMENT FAILED"));
        //actual call
        long orderId = orderService.placeOrder(orderRequest);
//        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> orderService.placeOrder(orderRequest));
//        Assertions.assertEquals("PAYMENT FAILED",runtimeException.getMessage());

        // verify call
        verify(orderRepository,times(2)).save(any());
        verify(productServiceFeignClient,times(1)).reduceQuantity(anyLong(),anyLong());
        verify(paymentServiceFeignClient,times(1)).doPayment(any(PaymentRequest.class));

        //assertion
        Assertions.assertEquals(orderEntity.getOrderId(),orderId);

    }


    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(4)
                .orderQuantity(1)
                .totalAmount(1499)
                .paymentMode(PaymentMode.CASH)
                .build();
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