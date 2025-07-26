package com.company.analyzer.service;

import com.company.analyzer.model.Employee;
import com.company.analyzer.util.EmployeeCSVLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;
public class EmployeeCSVLoaderTest {

    private EmployeeCSVLoader employeeCSVLoader;
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        employeeService = new EmployeeService();
        employeeCSVLoader = new EmployeeCSVLoader(employeeService);
    }

    @Test
    public void testLoadValidCSV() throws IOException {
        String csv = "id,firstName,lastName,salary,managerId\n" +
                "E001,John,Doe,100000,\n" +
                "E002,Jane,Smith,90000,E001\n";

        employeeCSVLoader.loadFromReader(new StringReader(csv));
        assertEquals(2, employeeService.getAllEmployees().size());

        Employee ceo = employeeService.getAllEmployees().get("E001");
        Employee emp = employeeService.getAllEmployees().get("E002");

        assertNotNull(ceo);
        assertNotNull(emp);
        assertEquals("Doe", ceo.getLastName());
        assertEquals(ceo.getId(), emp.getManagerId());
    }

    @Test
    public void testInvalidCSVThrowsException() {
        String invalidCsv = "id,firstName,lastName,salary,managerId\n" +
                "E001,John,,bad_salary,\n";

        assertThrows(IllegalArgumentException.class, () -> {
            employeeCSVLoader.loadFromReader(new StringReader(invalidCsv));
        });
    }

    @Test
    public void testMissingColumnsThrowsException() {
        String badLine = "id,firstName,lastName,salary,managerId\n" +
                "E001,John,Doe\n";

        assertThrows(IllegalArgumentException.class, () -> {
            employeeCSVLoader.loadFromReader(new StringReader(badLine));
        });
    }
}

