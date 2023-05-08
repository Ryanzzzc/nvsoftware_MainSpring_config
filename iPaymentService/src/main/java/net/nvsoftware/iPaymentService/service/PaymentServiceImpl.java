package net.nvsoftware.iPaymentService.service;

import lombok.extern.log4j.Log4j2;
import net.nvsoftware.iPaymentService.entity.PaymentEntity;
import net.nvsoftware.iPaymentService.model.PaymentMode;
import net.nvsoftware.iPaymentService.model.PaymentRequest;
import net.nvsoftware.iPaymentService.model.PaymentResponse;
import net.nvsoftware.iPaymentService.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService{
    @Autowired
    private PaymentRepository paymentRepository;
    @Override
    public long doPayment(PaymentRequest paymentRequest) {

        log.info("PaymentService doPayment start");
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .totalAmount(paymentRequest.getTotalAmount())
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS")
                .build();

        paymentRepository.save(paymentEntity);
        log.info("PaymentService doPayment done with paymentId: " + paymentEntity.getPaymentId());
        return paymentEntity.getPaymentId();
    }

    @Override
    public PaymentResponse getPaymentByOrderId(long orderId ) {
        log.info("OrderService getPaymentByOrderId start");

        PaymentEntity paymentEntity = null;
        try{
            paymentEntity = paymentRepository.findByOrderId(orderId);
        }catch(Exception e) {
            throw  new RuntimeException("getPaymentByOrderId with orderId "+orderId+"NOT FOUND" );
        }



        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(paymentEntity.getPaymentId())
                .orderId(paymentEntity.getOrderId())
                .paymentMode(PaymentMode.valueOf(paymentEntity.getPaymentMode()))
                .paymentDate(paymentEntity.getPaymentDate())
                .paymentStatus(paymentEntity.getPaymentStatus())
                .totalAmount(paymentEntity.getTotalAmount())
                .build();

        log.info("OrderService getPaymentByOrderId done");
        return paymentResponse;
    }
}
