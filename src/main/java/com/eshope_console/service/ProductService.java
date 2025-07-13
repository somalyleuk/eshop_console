package com.eshope_console.service;

import com.eshope_console.dao.ProductDAO;
import com.eshope_console.model.Product;

import java.time.LocalDateTime;
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

    /**
     * Insert a single product
     */
    public Product insertProduct(Product product) throws Exception {
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now());
        }
        return productDAO.insertProduct(product);
    }

    /**
     * Bulk insert products with progress tracking
     */
    public int bulkInsertProducts(List<Product> products) throws Exception {
        if (products == null || products.isEmpty()) {
            return 0;
        }

        System.out.println("Starting bulk insert of " + products.size() + " products...");
        long startTime = System.currentTimeMillis();

        int inserted = productDAO.bulkInsertProducts(products);

        long endTime = System.currentTimeMillis();
        double duration = (endTime - startTime) / 1000.0;
        System.out.println("Bulk insert completed: " + inserted + " products in " + duration + " seconds");
        System.out.println("Average: " + (inserted / duration) + " products/second");

        return inserted;
    }

    /**
     * Get products with pagination
     */
    public List<Product> getProductsWithPagination(int page, int pageSize) throws Exception {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 50;
        if (pageSize > 1000) pageSize = 1000; // Limit page size for performance

        return productDAO.findProductsWithPagination(page, pageSize);
    }

    /**
     * Get total product count
     */
    public long getTotalProductCount() throws Exception {
        return productDAO.getTotalProductCount();
    }

    /**
     * Search products with pagination
     */
    public List<Product> searchProductsWithPagination(String keyword, int page, int pageSize) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getProductsWithPagination(page, pageSize);
        }

        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 50;
        if (pageSize > 1000) pageSize = 1000;

        return productDAO.searchProductsWithPagination(keyword.trim(), page, pageSize);
    }

    /**
     * Get products by category with pagination
     */
    public List<Product> getProductsByCategoryWithPagination(String categoryId, int page, int pageSize) throws Exception {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 50;
        if (pageSize > 1000) pageSize = 1000;

        return productDAO.findProductsByCategoryWithPagination(categoryId, page, pageSize);
    }

    /**
     * Get products by price range with pagination
     */
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice, int page, int pageSize) throws Exception {
        if (minPrice < 0) minPrice = 0;
        if (maxPrice < minPrice) maxPrice = minPrice;
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 50;
        if (pageSize > 1000) pageSize = 1000;

        return productDAO.findProductsByPriceRange(minPrice, maxPrice, page, pageSize);
    }

    /**
     * Bulk update stock quantities
     */
    public int bulkUpdateStock(List<String> productCodes, List<Integer> newStocks) throws Exception {
        if (productCodes == null || newStocks == null || productCodes.size() != newStocks.size()) {
            throw new IllegalArgumentException("Product codes and stock quantities must have the same size and cannot be null");
        }

        System.out.println("Starting bulk stock update for " + productCodes.size() + " products...");
        long startTime = System.currentTimeMillis();

        int updated = productDAO.bulkUpdateStock(productCodes, newStocks);

        long endTime = System.currentTimeMillis();
        double duration = (endTime - startTime) / 1000.0;
        System.out.println("Bulk stock update completed: " + updated + " products in " + duration + " seconds");

        return updated;
    }

    /**
     * Generate sample products for testing bulk operations
     */
    public List<Product> generateSampleProducts(int count) {
        List<Product> products = new java.util.ArrayList<>();
        String[] categories = {"CAT001", "CAT002", "CAT003"};
        String[] productTypes = {"Smartphone", "Laptop", "Tablet", "Headphones", "Camera", "Speaker", "Watch", "Keyboard", "Mouse", "Monitor"};

        for (int i = 1; i <= count; i++) {
            Product product = new Product();
            product.setProductId(String.format("PRD%07d", i));
            product.setProductCode(String.format("PRD%07d", i));
            product.setProductName(productTypes[i % productTypes.length] + " " + i);
            product.setDescription("Sample product description for " + productTypes[i % productTypes.length] + " " + i);
            product.setCategoryId(categories[i % categories.length]);
            product.setPrice(10.0 + (i % 1000) * 0.1);
            product.setStockQuantity(10 + (i % 100));
            product.setCreatedAt(LocalDateTime.now());
            products.add(product);
        }

        return products;
    }

    /**
     * Get pagination info
     */
    public PaginationInfo getPaginationInfo(int currentPage, int pageSize, long totalCount) {
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        boolean hasNext = currentPage < totalPages;
        boolean hasPrevious = currentPage > 1;

        return new PaginationInfo(currentPage, pageSize, totalPages, totalCount, hasNext, hasPrevious);
    }

    /**
     * Pagination info class
     */
    public static class PaginationInfo {
        private final int currentPage;
        private final int pageSize;
        private final int totalPages;
        private final long totalCount;
        private final boolean hasNext;
        private final boolean hasPrevious;

        public PaginationInfo(int currentPage, int pageSize, int totalPages, long totalCount, boolean hasNext, boolean hasPrevious) {
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.totalPages = totalPages;
            this.totalCount = totalCount;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public long getTotalCount() {
            return totalCount;
        }

        public boolean hasNext() {
            return hasNext;
        }

        public boolean hasPrevious() {
            return hasPrevious;
        }
    }

    // Add this method to allow truncation from ReadProductService
    public void truncateProductsTable() throws Exception {
        productDAO.truncateProductsTable();
    }
}