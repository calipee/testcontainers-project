package com.testcontainers.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(parallel=true, disabledWithoutDocker=true)
class PresentationCustomerServiceTest {

  @Container
  PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:15-alpine")
    .withUsername("user")
    .withPassword("password");

  CustomerService customerService;

  // @BeforeAll
  // static void beforeAll() {
  // postgres.start();
  // }

  // @AfterAll
  // static void afterAll() {
  // postgres.stop();
  // }

  @BeforeEach
  void setUp() {
    DBConnectionProvider connectionProvider = new DBConnectionProvider(
        postgres.getJdbcUrl(),
        postgres.getUsername(),
        postgres.getPassword());
    customerService = new CustomerService(connectionProvider);
  }

  @Test
  void shouldGetCustomers() {
    customerService.createCustomer(new Customer(1L, "George"));
    customerService.createCustomer(new Customer(2L, "John"));

    List<Customer> customers = customerService.getAllCustomers();
    assertEquals(2, customers.size());
  }
  
  @Test
  void shouldGetSingleCustomer() {
    customerService.createCustomer(new Customer(3L, "Peter"));

    List<Customer> customers = customerService.getAllCustomers();
    assertEquals("Peter", customers.get(0).name());
  }

}
