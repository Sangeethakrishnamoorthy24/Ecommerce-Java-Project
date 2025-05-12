/**
 * 
 */
/**
 * 
 */
module Ecommerce {
    requires java.sql;
    requires org.junit.jupiter.api;
    
 // For JUnit platform
    requires org.junit.platform.commons;
    
    // Open packages for JUnit tests
    opens test to org.junit.platform.commons;
    
    // Expose your packages
    exports entity;
    exports dao;
    exports exception;
    exports util;
}
