package entity;

public class Product {
    private int productId;
    private String name;
    private double price;
    private String description;
    private int stock_quantity;

    // Default Constructor
    public Product() {}

    // Parameterized Constructor
    public Product(int productId, String name, double price, String description, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock_quantity = stockQuantity;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStockQuantity() {
        return stock_quantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stock_quantity = stockQuantity;
    }
}
