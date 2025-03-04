package com.hospital.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hospital.inventory.model.InventoryTransaction;
import com.hospital.inventory.model.Product;
import com.hospital.inventory.repository.InventoryTransactionRepository;
import com.hospital.inventory.repository.ProductRepository;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/transactions")
    public InventoryTransaction logTransaction(@RequestBody TransactionRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int currentStock = transactionRepository.findByProductId(product.getId())
                .stream()
                .mapToInt(InventoryTransaction::getQuantity)
                .sum();

        int requestedQuantity = request.getQuantity();
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (currentStock < requestedQuantity) {
            throw new IllegalArgumentException("Not enough stock available. Current stock: " + currentStock);
        }

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(product);
        transaction.setQuantity(-requestedQuantity);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setReason(request.getReason());
        transaction.setDate(LocalDateTime.now());
        transactionRepository.saveAndFlush(transaction); // Force immediate save
        return transaction;
    }

    @GetMapping("/inventory")
    public List<InventoryStatus> getInventory() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> {
            int stock = transactionRepository.findByProductId(product.getId())
                    .stream()
                    .mapToInt(InventoryTransaction::getQuantity)
                    .sum();
            return new InventoryStatus(product.getId(), product.getName(), stock);
        }).toList();
    }

    @GetMapping("/sales")
    public SalesDetails getSalesDetails(@RequestParam(required = false) String period) {
        List<InventoryTransaction> sales;

        if (period == null || period.isEmpty()) {
            sales = transactionRepository.findAll().stream()
                    .filter(t -> t.getTransactionType().equals("sale"))
                    .toList();
        } else {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start;

            switch (period.toLowerCase()) {
                case "daily":
                    start = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
                    break;
                case "weekly":
                    start = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                            .withHour(0).withMinute(0).withSecond(0).withNano(0);
                    break;
                case "monthly":
                    start = now.with(TemporalAdjusters.firstDayOfMonth())
                            .withHour(0).withMinute(0).withSecond(0).withNano(0);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid period: use 'daily', 'weekly', or 'monthly'");
            }

            sales = transactionRepository.findAll().stream()
                    .filter(t -> t.getTransactionType().equals("sale"))
                    .filter(t -> t.getDate().isAfter(start) && t.getDate().isBefore(now))
                    .toList();
        }

        // Debugging: Log the sales list
        System.out.println("Sales found: " + sales.size());
        sales.forEach(sale -> System.out.println("Sale: " + sale.getProduct().getName() + ", " + sale.getQuantity() + ", " + sale.getDate()));

        int totalSold = sales.stream().mapToInt(t -> -t.getQuantity()).sum();
        String displayPeriod = period == null || period.isEmpty() ? "All Time" : period.substring(0, 1).toUpperCase() + period.substring(1).toLowerCase();

        List<SaleTransaction> transactions = sales.stream()
                .map(t -> new SaleTransaction(
                        t.getProduct().getName(),
                        -t.getQuantity(),
                        t.getDate()
                ))
                .collect(Collectors.toList());

        return new SalesDetails(displayPeriod, totalSold, sales.size(), transactions);
    }
}

// Existing request class
class TransactionRequest {
    private Long productId;
    private int quantity;
    private String transactionType;
    private String reason;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

// Existing status class
class InventoryStatus {
    private Long productId;
    private String name;
    private int stock;

    public InventoryStatus(Long productId, String name, int stock) {
        this.productId = productId;
        this.name = name;
        this.stock = stock;
    }

    public Long getProductId() { return productId; }
    public String getName() { return name; }
    public int getStock() { return stock; }
}

// New detailed sales class
class SalesDetails {
    private String period;
    private int totalUnitsSold;
    private int numberOfSales;
    private List<SaleTransaction> transactions;

    public SalesDetails(String period, int totalUnitsSold, int numberOfSales, List<SaleTransaction> transactions) {
        this.period = period;
        this.totalUnitsSold = totalUnitsSold;
        this.numberOfSales = numberOfSales;
        this.transactions = transactions;
    }

    public String getPeriod() { return period; }
    public int getTotalUnitsSold() { return totalUnitsSold; }
    public int getNumberOfSales() { return numberOfSales; }
    public List<SaleTransaction> getTransactions() { return transactions; }
}

// New transaction detail class
class SaleTransaction {
    private String productName;
    private int quantity;
    private LocalDateTime date;

    public SaleTransaction(String productName, int quantity, LocalDateTime date) {
        this.productName = productName;
        this.quantity = quantity;
        this.date = date;
    }

    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getDate() { return date; }
}