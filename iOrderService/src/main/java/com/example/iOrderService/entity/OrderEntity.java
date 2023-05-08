package com.example.iOrderService.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long orderId;
    private long productId;
    private long orderQuantity;
    private long totalAmount;
    private String paymentMode;
    private Instant orderDate;
    private String orderStatus;
}
