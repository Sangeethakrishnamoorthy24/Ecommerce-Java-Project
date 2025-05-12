package main;

import java.util.*;
import dao.OrderProcessorRepository;
import dao.OrderProcessorRepositoryImpl;
import entity.Customer;
import entity.Product;
import exception.CustomerNotFoundException;
import exception.ProductNotFoundException;

public class MainModule {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OrderProcessorRepository orderProcessor = new OrderProcessorRepositoryImpl();

        while (true) {
            System.out.println("\n=== Ecommerce Application ===");
            System.out.println("1. Register Customer");
            System.out.println("2. Create Product");
            System.out.println("3. Delete Product");
            System.out.println("4. Add to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Place Order");
            System.out.println("7. View Customer Orders");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");
            
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    registerCustomer(scanner, orderProcessor);
                    break;
                case 2:
                    createProduct(scanner, orderProcessor);
                    break;
                case 3:
                    deleteProduct(scanner, orderProcessor);
                    break;
                case 4:
                    addToCart(scanner, orderProcessor);
                    break;
                case 5:
                    viewCart(scanner, orderProcessor);
                    break;
                case 6:
                    placeOrder(scanner, orderProcessor);
                    break;
                case 7:
                    viewCustomerOrders(scanner, orderProcessor);
                    break;
                case 8:
                    System.out.println("Exiting the application...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // 1. Register Customer
    private static void registerCustomer(Scanner scanner, OrderProcessorRepository orderProcessor) {
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        System.out.print("Enter customer email: ");
        String email = scanner.nextLine();
        System.out.print("Enter customer password: ");
        String password = scanner.nextLine();

        Customer customer = new Customer(0, name, email, password); // 0 for customer_id because it will be auto-incremented
        boolean result = orderProcessor.createCustomer(customer);
        if (result) {
            System.out.println("Customer registered successfully!");
        } else {
            System.out.println("Failed to register customer.");
        }
    }

    // 2. Create Product
    private static void createProduct(Scanner scanner, OrderProcessorRepository orderProcessor) {
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter product price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter product description: ");
        String description = scanner.nextLine();
        System.out.print("Enter product stock quantity: ");
        int stock_quantity = scanner.nextInt();

        Product product = new Product(0, name, price, description, stock_quantity); // 0 for product_id because it will be auto-incremented
        boolean result = orderProcessor.createProduct(product);
        if (result) {
            System.out.println("Product created successfully!");
        } else {
            System.out.println("Failed to create product.");
        }
    }

    // 3. Delete Product
    private static void deleteProduct(Scanner scanner, OrderProcessorRepository orderProcessor) {
        System.out.print("Enter product ID to delete: ");
        int productId = scanner.nextInt();
        try {
            boolean result = orderProcessor.deleteProduct(productId);
            if (result) {
                System.out.println("Product deleted successfully.");
            } else {
                System.out.println("Failed to delete product.");
            }
        } catch (ProductNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // 4. Add to Cart
    private static void addToCart(Scanner scanner, OrderProcessorRepository orderProcessor) {
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();
        System.out.print("Enter product ID: ");
        int productId = scanner.nextInt();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            Customer customer = new Customer(customerId, "", "", "");  // Dummy customer object with only ID
            Product product = new Product(productId, "", 0.0, "", 0);  // Dummy product object with only ID
            boolean result = orderProcessor.addToCart(customer, product, quantity);
            if (result) {
                System.out.println("Product added to cart successfully!");
            } else {
                System.out.println("Failed to add product to cart.");
            }
        } catch (CustomerNotFoundException | ProductNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // 5. View Cart
    private static void viewCart(Scanner scanner, OrderProcessorRepository orderProcessor) {
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Customer customer = new Customer(customerId, "", "", ""); // Dummy customer object with only ID
        List<Product> products = orderProcessor.getAllFromCart(customer);
        if (products.isEmpty()) {
            System.out.println("Cart is empty.");
        } else {
            System.out.println("Products in cart:");
            for (Product product : products) {
                System.out.println("Product ID: " + product.getProductId() + ", Name: " + product.getName() +
                                   ", Price: " + product.getPrice() + ", Quantity: " + product.getStockQuantity());
            }
        }
    }

    // 6. Place Order
    private static void placeOrder(Scanner scanner, OrderProcessorRepository orderProcessor) {
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter shipping address: ");
        String shippingAddress = scanner.nextLine();

        Customer customer = new Customer(customerId, "", "", "");  // Dummy customer object with only ID

        // Dummy product with quantity to simulate placing an order
        List<Map<Product, Integer>> productsWithQuantity = new ArrayList<>();
        System.out.print("Enter the number of products in the order: ");
        int numProducts = scanner.nextInt();
        for (int i = 0; i < numProducts; i++) {
            System.out.print("Enter product ID: ");
            int productId = scanner.nextInt();
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();

            Product product = new Product(productId, "", 0.0, "", 0); // Dummy product with only ID
            Map<Product, Integer> productQuantityMap = new HashMap<>();
            productQuantityMap.put(product, quantity);
            productsWithQuantity.add(productQuantityMap);
        }

        boolean result = orderProcessor.placeOrder(customer, productsWithQuantity, shippingAddress);
        if (result) {
            System.out.println("Order placed successfully!");
        } else {
            System.out.println("Failed to place order.");
        }
    }

    // 7. View Customer Orders
    private static void viewCustomerOrders(Scanner scanner, OrderProcessorRepository orderProcessor) {
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        List<Map<Product, Integer>> orders = orderProcessor.getOrdersByCustomer(customerId);
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
        } else {
            System.out.println("Orders for customer ID " + customerId + ":");
            for (Map<Product, Integer> order : orders) {
                for (Product product : order.keySet()) {
                    System.out.println("Product ID: " + product.getProductId() + ", Name: " + product.getName() +
                                       ", Quantity: " + order.get(product));
                }
            }
        }
    }
}
