package com.testcontainers.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

class PresentationCustomerServiceWithoutModuleTest {

  GenericContainer<?> postgres = new GenericContainer<>(
    DockerImageName.parse("postgres:15-alpine"))
    .withExposedPorts(5432)
    .withEnv("POSTGRES_PASSWORD", "password")
    .withEnv("POSTGRES_USER", "user")
    .withEnv("POSTGRES_DB", "test");

  CustomerService customerService;

// @BeforeAll
// static void beforeAll() {
//   postgres.start();
// }

  @AfterEach
    void afterAll() {
    postgres.stop();
  }

  @BeforeEach
    void setUp() {
    postgres.start();
    String jdbcUrl = "jdbc:postgresql://" + postgres.getHost() + ":" + postgres.getFirstMappedPort() + "/test";
    DBConnectionProvider connectionProvider = new DBConnectionProvider(
        jdbcUrl,
        "user",
        "password");
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
    customerService.createCustomer(new Customer(1L, "Peter"));

    List<Customer> customers = customerService.getAllCustomers();
    assertEquals("Peter", customers.get(0).name());
  }
}
