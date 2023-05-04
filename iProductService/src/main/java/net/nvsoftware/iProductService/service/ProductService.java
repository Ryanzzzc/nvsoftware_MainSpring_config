package net.nvsoftware.iProductService.service;

import net.nvsoftware.iProductService.model.ProductRequest;
import net.nvsoftware.iProductService.model.ProductResponse;

public interface ProductService {
    public long addProduct(ProductRequest productRequest);
    ProductResponse getProductById(long id);
}
