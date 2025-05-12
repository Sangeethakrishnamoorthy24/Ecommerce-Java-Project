package dao;

import java.sql.*;
import java.util.*;

import entity.Customer;
import entity.Product;
import exception.CustomerNotFoundException;
import exception.ProductNotFoundException;
import util.DBConnUtil;

public class OrderProcessorRepositoryImpl implements OrderProcessorRepository {

    private Connection connection;

    public OrderProcessorRepositoryImpl() {
        this.connection = DBConnUtil.getConnection();
    }

    @Override
    public boolean createProduct(Product product) {
        String query = "INSERT INTO products (name, price, description, stock_quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getDescription());
            stmt.setInt(4, product.getStockQuantity());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean createCustomer(Customer customer) {
        String checkEmailQuery = "SELECT 1 FROM customers WHERE email = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkEmailQuery)) {
            checkStmt.setString(1, customer.getEmail());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                throw new IllegalArgumentException("Email already exists: " + customer.getEmail());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String query = "INSERT INTO customers (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteProduct(int productId) throws ProductNotFoundException {
        if (!productExists(productId)) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
        String query = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteCustomer(int customerId) {
        String query = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
            }
            return true;
        } catch (SQLException | CustomerNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addToCart(Customer customer, Product product, int quantity)
            throws CustomerNotFoundException, ProductNotFoundException {
        if (!customerExists(customer.getCustomerId())) {
            throw new CustomerNotFoundException("Customer with ID " + customer.getCustomerId() + " not found.");
        }
        if (!productExists(product.getProductId())) {
            throw new ProductNotFoundException("Product with ID " + product.getProductId() + " not found.");
        }

        String query = "INSERT INTO cart (customer_id, product_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customer.getCustomerId());
            stmt.setInt(2, product.getProductId());
            stmt.setInt(3, quantity);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean customerExists(int customerId) {
        String query = "SELECT 1 FROM customers WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean productExists(int productId) {
        String query = "SELECT 1 FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeFromCart(Customer customer, Product product) {
        String query = "DELETE FROM cart WHERE customer_id = ? AND product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customer.getCustomerId());
            stmt.setInt(2, product.getProductId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Product> getAllFromCart(Customer customer) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.product_id, p.name, p.price, c.quantity " +
                       "FROM cart c JOIN products p ON c.product_id = p.product_id " +
                       "WHERE c.customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customer.getCustomerId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    "",
                    rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public boolean placeOrder(Customer customer, List<Map<Product, Integer>> productsWithQuantity, String shippingAddress) {
        String fetchProductPrice = "SELECT price FROM products WHERE product_id = ?";
        String insertOrderQuery = "INSERT INTO orders (customer_id, order_date, total_price, shipping_address) VALUES (?, NOW(), ?, ?)";
        String insertOrderItemsQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        double totalPrice = 0;

        try {
            connection.setAutoCommit(false);

            for (Map<Product, Integer> map : productsWithQuantity) {
                for (Product product : map.keySet()) {
                    try (PreparedStatement stmt = connection.prepareStatement(fetchProductPrice)) {
                        stmt.setInt(1, product.getProductId());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                double price = rs.getDouble("price");
                                product.setPrice(price);
                                totalPrice += price * map.get(product);
                            }
                        }
                    }
                }
            }

            int orderId = 0;
            try (PreparedStatement stmt = connection.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, customer.getCustomerId());
                stmt.setDouble(2, totalPrice);
                stmt.setString(3, shippingAddress);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }
            }

            for (Map<Product, Integer> map : productsWithQuantity) {
                for (Product product : map.keySet()) {
                    try (PreparedStatement stmt = connection.prepareStatement(insertOrderItemsQuery)) {
                        stmt.setInt(1, orderId);
                        stmt.setInt(2, product.getProductId());
                        stmt.setInt(3, map.get(product));
                        stmt.setDouble(4, product.getPrice());
                        stmt.executeUpdate();
                    }
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public List<Map<Product, Integer>> getOrdersByCustomer(int customerId) {
        List<Map<Product, Integer>> orderList = new ArrayList<>();
        String query = "SELECT o.order_id, p.product_id, p.name, p.price, oi.quantity, p.description, p.stock_quantity " +
                       "FROM orders o " +
                       "JOIN order_items oi ON o.order_id = oi.order_id " +
                       "JOIN products p ON oi.product_id = p.product_id " +
                       "WHERE o.customer_id = ? ORDER BY o.order_id";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            int currentOrderId = -1;
            Map<Product, Integer> currentOrder = null;

            while (rs.next()) {
                int orderId = rs.getInt("order_id");

                if (orderId != currentOrderId) {
                    currentOrder = new LinkedHashMap<>();
                    orderList.add(currentOrder);
                    currentOrderId = orderId;
                }

                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getInt("stock_quantity")
                );

                currentOrder.put(product, rs.getInt("quantity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderList;
    }

    @Override
    public Product getProductById(int productId) throws ProductNotFoundException {
        String query = "SELECT * FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getInt("stock_quantity")
                );
            } else {
                throw new ProductNotFoundException("Product with ID " + productId + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ProductNotFoundException("Error fetching product details");
        }
    }

    
}
