package com.eshope_console.service;

import com.eshope_console.model.Product;
import com.eshope_console.util.ConsoleColors;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadProductService {
    private final ProductService productService;
    private final ExecutorService executorService;

    public ReadProductService(ProductService productService) {
        this.productService = productService;
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public void insert10MillionProducts() {
        System.out.println(ConsoleColors.CYAN_BOLD + "\n🚀 Starting 10 Million Products Insert Operation" + ConsoleColors.RESET);
        System.out.println("This operation will create 10,000,000 sample products in the database.");
        System.out.println("The operation will be performed in batches for optimal performance.\n");

        try {
            long startTime = System.currentTimeMillis();
            int totalProducts = 10_000_000;
            int batchSize = 10_000;
            int totalBatches = (int) Math.ceil((double) totalProducts / batchSize);

            AtomicInteger completedBatches = new AtomicInteger(0);
            AtomicInteger totalInserted = new AtomicInteger(0);

            System.out.println("📊 Operation Details:");
            System.out.println("   Total Products: " + String.format("%,d", totalProducts));
            System.out.println("   Batch Size: " + String.format("%,d", batchSize));
            System.out.println("   Total Batches: " + String.format("%,d", totalBatches));
            System.out.println("   Parallel Threads: 4\n");
            System.out.println("🚀 Starting bulk insert operations...\n");

            for (int batch = 0; batch < totalBatches; batch++) {
                final int batchNumber = batch;
                CompletableFuture.runAsync(() -> {
                    try {
                        int startIndex = batchNumber * batchSize;
                        int endIndex = Math.min(startIndex + batchSize, totalProducts);
                        int currentBatchSize = endIndex - startIndex;

                        List<Product> products = productService.generateSampleProducts(currentBatchSize);
                        for (int i = 0; i < products.size(); i++) {
                            Product product = products.get(i);
                            String productId = String.format("P%09d", startIndex + i + 1);
                            product.setProductId(productId);
                            product.setProductCode(productId);
                        }

                        int inserted = productService.bulkInsertProducts(products);
                        totalInserted.addAndGet(inserted);

                        int completed = completedBatches.incrementAndGet();
                        double progress = (completed * 100.0) / totalBatches;
                        if (completed % 10 == 0 || completed == totalBatches) {
                            System.out.printf(ConsoleColors.GREEN_BOLD + "✅ Batch %d/%d completed (%.1f%%) - Inserted: %,d products%n" + ConsoleColors.RESET,
                                    completed, totalBatches, progress, inserted);
                        }

                    } catch (Exception e) {
                        System.err.printf(ConsoleColors.RED_BOLD + "❌ Error in batch %d: %s%n" + ConsoleColors.RESET, batchNumber, e.getMessage());
                    }
                }, executorService);
            }

            System.out.println(ConsoleColors.CYAN + "\n⏳ Waiting for all batches to complete..." + ConsoleColors.RESET);
            while (completedBatches.get() < totalBatches) {
                Thread.sleep(1000);
            }

            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.0;

            System.out.println(ConsoleColors.CYAN_BOLD + "\n🎉 10 Million Products Insert Operation Completed!" + ConsoleColors.RESET);
            System.out.println("📈 Performance Summary:");
            System.out.println("   Total Inserted: " + String.format("%,d", totalInserted.get()) + " products");
            System.out.println("   Total Time: " + String.format("%.2f", duration) + " seconds");
            System.out.println("   Average Speed: " + String.format("%.0f", totalInserted.get() / duration) + " products/second");
            System.out.println("   Database Size: ~" + String.format("%.1f", (totalInserted.get() * 0.5) / 1024 / 1024) + " MB estimated\n");
            System.out.println(ConsoleColors.GREEN_BOLD + "🎉 Insert operation completed successfully!" + ConsoleColors.RESET);
            System.out.println("Press Enter to return to menu...");
            try {
                System.in.read();
            } catch (Exception ignored) {
            }

        } catch (Exception e) {
            System.err.println(ConsoleColors.RED_BOLD + "Error during bulk insert operation: " + e.getMessage() + ConsoleColors.RESET);
            e.printStackTrace();
            System.out.println("Press Enter to return to menu...");
            try {
                System.in.read();
            } catch (Exception ignored) {
            }
        } finally {
            executorService.shutdown();
        }
    }

    public void testReadingPerformance() {
        System.out.println(ConsoleColors.CYAN_BOLD + "\n📖 Testing Reading Performance with 10M Products" + ConsoleColors.RESET);

        try {
            long totalCount = productService.getTotalProductCount();
            System.out.println("📊 Total products in database: " + String.format("%,d", totalCount));

            if (totalCount == 0) {
                System.out.println(ConsoleColors.YELLOW + "⚠️  No products found. Please run the bulk insert operation first." + ConsoleColors.RESET);
                return;
            }

            int[] pageSizes = {50, 100, 500, 1000};

            for (int pageSize : pageSizes) {
                System.out.println("\n🔍 Testing with page size: " + pageSize);

                long startTime = System.currentTimeMillis();
                List<Product> products = productService.getProductsWithPagination(1, pageSize);
                long endTime = System.currentTimeMillis();

                double duration = (endTime - startTime) / 1000.0;
                System.out.printf("   Retrieved %d products in %.3f seconds%n", products.size(), duration);
                System.out.printf("   Speed: %.0f products/second%n", products.size() / duration);
            }


            System.out.println("\n🔍 Testing search performance...");
            String[] searchTerms = {"Smartphone", "Laptop", "Electronics"};

            for (String term : searchTerms) {
                long startTime = System.currentTimeMillis();
                List<Product> results = productService.searchProductsWithPagination(term, 1, 100);
                long endTime = System.currentTimeMillis();

                double duration = (endTime - startTime) / 1000.0;
                System.out.printf("   Search '%s': %d results in %.3f seconds%n", term, results.size(), duration);
            }

        } catch (Exception e) {
            System.err.println(ConsoleColors.RED_BOLD + "Error during reading performance test: " + e.getMessage() + ConsoleColors.RESET);
            e.printStackTrace();
        }
    }

    public void createPerformanceIndexes() {
        System.out.println(ConsoleColors.CYAN_BOLD + "\n🔧 Creating Performance Indexes" + ConsoleColors.RESET);

        try {

            System.out.println("📋 Recommended indexes for 10M products:");
            System.out.println("   CREATE INDEX idx_products_name ON products(name);");
            System.out.println("   CREATE INDEX idx_products_category ON products(category_id);");
            System.out.println("   CREATE INDEX idx_products_price ON products(price);");
            System.out.println("   CREATE INDEX idx_products_created_at ON products(created_at);");
            System.out.println("   CREATE INDEX idx_products_name_lower ON products(LOWER(name));");
            System.out.println("   CREATE INDEX idx_products_stock ON products(stock_quantity);");

            System.out.println(ConsoleColors.GREEN_BOLD + "\n✅ Index creation recommendations displayed." + ConsoleColors.RESET);
            System.out.println("💡 Run these SQL commands in your database for optimal performance.");

        } catch (Exception e) {
            System.err.println(ConsoleColors.RED_BOLD + "Error during index creation: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    public void insert10MillionProducts(boolean shouldTruncate) {
        try {
            if (shouldTruncate) {
                System.out.println(ConsoleColors.YELLOW_BOLD + "\n⚠️  Truncating products table before insert..." + ConsoleColors.RESET);
                productService.truncateProductsTable();
                System.out.println(ConsoleColors.GREEN_BOLD + "\n✅ Products table truncated." + ConsoleColors.RESET);
            }
        } catch (Exception e) {
            System.err.println(ConsoleColors.RED_BOLD + "Error truncating products table: " + e.getMessage() + ConsoleColors.RESET);
            return;
        }
        insert10MillionProducts();
    }


    public void read10MillionProducts() {
        System.out.println(ConsoleColors.CYAN_BOLD + "\n📖 Reading 10 Million Products from Database" + ConsoleColors.RESET);
        try {
            long totalCount = productService.getTotalProductCount();
            System.out.println("📊 Total products in database: " + String.format("%,d", totalCount));
            if (totalCount == 0) {
                System.out.println(ConsoleColors.YELLOW + "⚠️  No products found. Please insert products first." + ConsoleColors.RESET);
                return;
            }
            long startTime = System.currentTimeMillis();

            int pageSize = 10000;
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            long readCount = 0;
            for (int page = 1; page <= totalPages; page++) {
                List<Product> products = productService.getProductsWithPagination(page, pageSize);
                readCount += products.size();
                if (page == 1 && !products.isEmpty()) {
                    System.out.println("\nSample Products:");
                    for (int i = 0; i < Math.min(5, products.size()); i++) {
                        Product p = products.get(i);
                        System.out.println("- " + p.getProductId() + ": " + p.getProductName());
                    }
                }
            }
            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.0;
            System.out.println("\n✅ Read completed. Total records read: " + String.format("%,d", readCount));
            System.out.println("⏱️  Time taken: " + String.format("%.2f", duration) + " seconds");
            System.out.println("Speed: " + String.format("%.0f", readCount / duration) + " records/second");
            System.out.println(ConsoleColors.GREEN_BOLD + "\n🎉 Read operation completed successfully!" + ConsoleColors.RESET);
            System.out.println("Press Enter to return to menu...");
            try {
                System.in.read();
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            System.err.println(ConsoleColors.RED_BOLD + "Error reading products: " + e.getMessage() + ConsoleColors.RESET);
            System.out.println("Press Enter to return to menu...");
            try {
                System.in.read();
            } catch (Exception ignored) {
            }
        }
    }
} 