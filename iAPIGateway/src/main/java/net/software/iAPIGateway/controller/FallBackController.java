package net.software.iAPIGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackController {
    @GetMapping("/orderServiceFallback")
    public String orderServiceFallBack() {
        return "orderService down";
    }

    @GetMapping("/productServiceFallback")
    public String productServiceFallBack() {
        return "productService down";
    }

    @GetMapping("/paymentServiceFallback")
    public String paymentServiceFallBack() {
        return "paymentService down";
    }

}
