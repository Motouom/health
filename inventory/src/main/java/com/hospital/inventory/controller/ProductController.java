package com.hospital.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.hospital.inventory.model.Product;
import com.hospital.inventory.model.InventoryTransaction;
import com.hospital.inventory.repository.ProductRepository;
import com.hospital.inventory.repository.InventoryTransactionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    @PostMapping("/products")
    @Transactional
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequest request) {
        try {
            logger.info("Starting to add product: {}", request.getName());
            logger.debug("Request payload: {}", request);

            Optional<Product> existingProduct = productRepository.findAll().stream()
                    .filter(p -> p.getName().equals(request.getName()))
                    .findFirst();
            logger.info("Existing product check: {}", existingProduct.isPresent() ? "Found" : "Not found");

            Product product;
            if (existingProduct.isPresent()) {
                product = existingProduct.get();
                logger.info("Using existing product with ID: {}", product.getId());
            } else {
                product = new Product();
                product.setName(request.getName());
                product.setDescription(request.getDescription());
                product.setCostPrice(request.getCostPrice());
                product.setSellingPrice(request.getSellingPrice());
                product.setSupplier(request.getSupplier());

                logger.info("Saving new product...");
                product = productRepository.saveAndFlush(product);
                logger.info("New product saved with ID: {}", product.getId());

                Optional<Product> savedCheck = productRepository.findById(product.getId());
                if (!savedCheck.isPresent()) {
                    logger.error("Product ID {} not found in database after save!", product.getId());
                    throw new RuntimeException("Failed to persist product with ID: " + product.getId());
                }
                logger.info("Verified product ID {} exists in database", product.getId());
            }

            logger.info("Creating transaction for product ID: {}", product.getId());
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setProduct(product);
            transaction.setQuantity(request.getInitialQuantity());
            transaction.setTransactionType("entry");
            transaction.setDate(LocalDateTime.now());
            InventoryTransaction savedTransaction = transactionRepository.saveAndFlush(transaction);
            logger.info("Transaction saved with ID: {} for product ID: {}", savedTransaction.getId(), product.getId());

            logger.info("Product addition completed successfully for: {}", product.getName());
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            logger.error("Failed to add product '{}': {}", request.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.info("Fetching product with ID: {}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            logger.info("Product found with ID: {}", id);
            return ResponseEntity.ok(product.get());
        } else {
            logger.warn("Product not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        List<InventoryTransaction> transactions = transactionRepository.findByProductId(id);
        if (!transactions.isEmpty()) {
            transactionRepository.deleteAll(transactions);
        }

        productRepository.delete(product);
    }

    @PutMapping("/products/{id}")
    @Transactional
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        logger.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update basic fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCostPrice(request.getCostPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setSupplier(request.getSupplier());

        // Calculate current stock
        int currentStock = transactionRepository.findByProductId(id)
                .stream()
                .mapToInt(InventoryTransaction::getQuantity)
                .sum();
        logger.info("Current stock for product ID {}: {}", id, currentStock);

        // Adjust stock if initialQuantity differs
        int newStock = request.getInitialQuantity();
        if (newStock != currentStock) {
            int stockChange = newStock - currentStock;
            logger.info("Stock change for product ID {}: {}", id, stockChange);

            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setProduct(product);
            transaction.setQuantity(stockChange);
            transaction.setTransactionType(stockChange > 0 ? "entry" : "adjustment");
            transaction.setReason("Stock update via product edit");
            transaction.setDate(LocalDateTime.now());
            transactionRepository.saveAndFlush(transaction);
            logger.info("Stock adjustment transaction saved for product ID: {}", id);
        }

        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated successfully with ID: {}", id);
        return updatedProduct;
    }
}

class ProductRequest {
    private String name;
    private String description;
    private double costPrice;
    private double sellingPrice;
    private String supplier;
    private int initialQuantity;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public int getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(int initialQuantity) { this.initialQuantity = initialQuantity; }

    @Override
    public String toString() {
        return "ProductRequest{name='" + name + "', description='" + description + "', costPrice=" + costPrice +
                ", sellingPrice=" + sellingPrice + ", supplier='" + supplier + "', initialQuantity=" + initialQuantity + "}";
    }
}