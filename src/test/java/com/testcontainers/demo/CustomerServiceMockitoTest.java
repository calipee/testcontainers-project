package com.testcontainers.demo;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CustomerServiceMockitoTest {

  @Mock
  private DBConnectionProvider dbConnectionProvider;
  @Mock
  private Connection connection;
  @Mock
  private PreparedStatement preparedStatement;
  @Mock
  private ResultSet resultSet;

  private CustomerService customerService;

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    when(dbConnectionProvider.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true, true, false);

    when(resultSet.getLong("id")).thenReturn(1L, 2L);
    when(resultSet.getString("name")).thenReturn("George", "John");

    customerService = new CustomerService(dbConnectionProvider);
  }

  @Test
  void shouldGetCustomers() throws Exception {

    customerService.createCustomer(new Customer(1L, "George"));
    customerService.createCustomer(new Customer(2L, "John"));

    List<Customer> customers = customerService.getAllCustomers();
    assertEquals(2, customers.size());

    // Ensure that matchers are used consistently within the verify calls
    verify(preparedStatement, times(2)).setLong(eq(1), anyLong());
    verify(preparedStatement, times(2)).setString(eq(2), anyString());
    verify(preparedStatement, times(3)).execute();
    verify(resultSet, times(2)).getLong(eq("id"));
    verify(resultSet, times(2)).getString(eq("name"));
  }

}
