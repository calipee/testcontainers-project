package com.testcontainers.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
public class CustomerServiceTestDockerCompose {

  @Container
  public static ComposeContainer  environment = new ComposeContainer(new File("src/test/resources/docker-compose.yml"))
      .withExposedService("db-1", 5432, Wait.forLogMessage("database system is ready to accept connections", 0));
      // .withLocalCompose(true); // FIXME geht vllt nicht

  CustomerService customerService;

  // @BeforeAll
  // static void beforeAll() {
  //   // environment = new DockerComposeContainer(new File("docker-compose.yml"))
  //   //     .withExposedService("db", 5432, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));
  //   // // .withLocalCompose(true); // FIXME geht vllt nicht
  // }

  @AfterAll
  static void afterAll() {
    // environment.stop();
    environment.close();
  }

  @BeforeEach
  void setUp() {
    DBConnectionProvider connectionProvider = new DBConnectionProvider(
        "jdbc:postgresql://" + environment.getServiceHost("db", 5432) + ":"
            + environment.getServicePort("db", 5432) + "/postgres",
        "postgres",
        "postgres");
    customerService = new CustomerService(connectionProvider);
  }

  @Test
  void shouldGetCustomers() {
    customerService.createCustomer(new Customer(1L, "George"));
    customerService.createCustomer(new Customer(2L, "John"));

    List<Customer> customers = customerService.getAllCustomers();
    assertEquals(2, customers.size());
  }
}
