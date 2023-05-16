package com.example.iOrderService;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceInstanceListSupplierTest implements ServiceInstanceListSupplier {
    @Override
    public String getServiceId() {
        return null;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        List<ServiceInstance> serviceInstanceList = new ArrayList<>();

        serviceInstanceList.add(new DefaultServiceInstance(
                "PAYMENT SERVICE",
                "",
                "localhost",
                8003,
                false
        ));

        serviceInstanceList.add(new DefaultServiceInstance(
                "PAYMENT SERVICE",
                "",
                "localhost",
                8003,
                false
        ));

        return Flux.just(serviceInstanceList);
    }
}
