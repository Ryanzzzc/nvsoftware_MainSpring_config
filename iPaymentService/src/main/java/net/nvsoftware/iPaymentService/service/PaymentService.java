package net.nvsoftware.iPaymentService.service;

import net.nvsoftware.iPaymentService.entity.PaymentEntity;
import net.nvsoftware.iPaymentService.model.PaymentRequest;
import net.nvsoftware.iPaymentService.model.PaymentResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentService{
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentByOrderId(long orderId);
}
