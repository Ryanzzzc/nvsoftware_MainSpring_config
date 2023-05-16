package com.example.iOrderService;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

@TestConfiguration

public class OrderServiceConfigTest {
    public ServiceInstanceListSupplier supplier() {
        return new ServiceInstanceListSupplierTest();
    }
}
