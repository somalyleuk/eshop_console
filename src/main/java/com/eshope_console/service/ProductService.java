package com.eshope_console.service;

import com.eshope_console.dao.ProductDAO;
import com.eshope_console.model.Product;

import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> getAllProducts() throws Exception {
        return productDAO.findAllWithCategories();
    }

    public List<Product> searchProducts(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productDAO.searchByNameOrCategory(keyword.trim());
    }

    public Product getProductByCode(String code) throws Exception {
        return productDAO.findByCode(code);
    }

    public boolean updateProductStock(String productCode, int newStock) throws Exception {
        return productDAO.updateStock(productCode, newStock);
    }
}