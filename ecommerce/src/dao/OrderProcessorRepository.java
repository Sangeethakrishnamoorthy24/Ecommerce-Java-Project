package dao;

import java.util.List;
import java.util.Map;
import entity.Customer;
import entity.Product;
import exception.CustomerNotFoundException;
import exception.ProductNotFoundException;

public interface OrderProcessorRepository {
    
    // Create Product
    boolean createProduct(Product product);

    // Create Customer
    boolean createCustomer(Customer customer);

    // Delete Product by ID
    boolean deleteProduct(int productId) throws ProductNotFoundException;

    // Delete Customer by ID
    boolean deleteCustomer(int customerId);

    // Add product to Cart
    boolean addToCart(Customer customer, Product product, int quantity) throws CustomerNotFoundException, ProductNotFoundException;

    // Remove product from Cart
    boolean removeFromCart(Customer customer, Product product);

    // Get all products in Cart for a specific Customer
    List<Product> getAllFromCart(Customer customer);

    // Place an Order
    boolean placeOrder(Customer customer, List<Map<Product, Integer>> productsWithQuantity, String shippingAddress);

    // Get Orders by Customer ID
    List<Map<Product, Integer>> getOrdersByCustomer(int customerId);

    // Get Product by ID
    Product getProductById(int productId) throws ProductNotFoundException; // Add this method
}
