package net.nvsoftware.iPaymentService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long paymentId;
    private long orderId;
    private String paymentMode;
    private String referenceNumber;
    private long totalAmount;
    private Instant paymentDate;
    private String paymentStatus;

}
