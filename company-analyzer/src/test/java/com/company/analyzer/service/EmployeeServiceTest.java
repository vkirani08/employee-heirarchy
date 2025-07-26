package com.company.analyzer.service;

import com.company.analyzer.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {

    private EmployeeService employeeService;

    @BeforeEach
    public void setup() {
        employeeService = new EmployeeService();
    }

    @Test
    public void testAddEmployeeAndBuildHierarchy() {
        Employee ceo = new Employee("E001", "John", "Doe", 200000, null);
        Employee manager = new Employee("E002", "Jane", "Smith", 120000, "E001");
        Employee employee = new Employee("E003", "Alice", "Brown", 90000, "E002");

        employeeService.addEmployee(ceo);
        employeeService.addEmployee(manager);
        employeeService.addEmployee(employee);

        employeeService.buildHierarchy();

        assertEquals(ceo, employeeService.getRoot());
        assertTrue(ceo.getSubordinates().contains(manager));
        assertTrue(manager.getSubordinates().contains(employee));
    }

    @Test
    public void testGetRoot() {
        Employee ceo = new Employee("E001", "CEO", "Boss", 200000, null);
        employeeService.addEmployee(ceo);
        employeeService.buildHierarchy();

        Employee root = employeeService.getRoot();
        assertNotNull(root);
        assertEquals("E001", root.getId());
    }

    @Test
    public void testAnalyzeManagerSalaries_validRange() {
        Employee ceo = new Employee("E001", "John", "Doe", 140000, null);
        Employee manager = new Employee("E002", "Jane", "Smith", 100000, "E001");
        Employee subordinate = new Employee("E003", "Alice", "Brown", 80000, "E002");

        employeeService.addEmployee(ceo);
        employeeService.addEmployee(manager);
        employeeService.addEmployee(subordinate);

        employeeService.buildHierarchy();

        List<String> violations = employeeService.analyzeManagerSalaries(ceo);

        // In this case, all salaries are within valid range. So, we expect no violations.
        assertTrue(violations.isEmpty(), "Expected no salary violations");
    }

    @Test
    public void testAnalyzeManagerSalaries_withViolations() {
        Employee ceo = new Employee("E001", "John", "Doe", 140000, null);
        Employee manager = new Employee("E002", "Jane", "Smith", 50000, "E001"); // too low
        Employee subordinate1 = new Employee("E003", "Alice", "Brown", 80000, "E002");
        Employee subordinate2 = new Employee("E004", "Bob", "White", 90000, "E002");

        employeeService.addEmployee(ceo);
        employeeService.addEmployee(manager);
        employeeService.addEmployee(subordinate1);
        employeeService.addEmployee(subordinate2);

        employeeService.buildHierarchy();

        List<String> violations = employeeService.analyzeManagerSalaries(ceo);

        assertEquals(2, violations.size());
        for (String s : violations) {
            System.out.println(s);
        }
        assertTrue(violations.get(0).contains("John Doe earns"));
        assertTrue(violations.get(1).contains("Jane Smith earns"));
    }

    @Test
    public void testAnalyzeReportingLineDepth() {
        Employee ceo = new Employee("E001", "CEO", "Top", 200000, null);
        Employee manager = new Employee("E002", "Manager", "One", 100000, "E001");
        Employee employee = new Employee("E003", "Worker", "Bee", 60000, "E002");

        employeeService.addEmployee(ceo);
        employeeService.addEmployee(manager);
        employeeService.addEmployee(employee);
        employeeService.buildHierarchy();

        Map<String, Integer> result = employeeService.analyzeReportingLineDepth(ceo, 1);

        // Since max depth is 3 here (CEO->Manager->Worker), nothing should exceed 4
        assertTrue(result.isEmpty());

        // Now add more depth to trigger a violation
        Employee level4 = new Employee("E004", "Intern", "Joe", 30000, "E003");
        Employee level5 = new Employee("E005", "Contractor", "Smith", 25000, "E004");

        employeeService.addEmployee(level4);
        employeeService.addEmployee(level5);
        employeeService.buildHierarchy();

        result = employeeService.analyzeReportingLineDepth(ceo, 1);

        // E005 should be at depth 5, which exceeds the threshold
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Contractor Smith"));
        assertEquals(5, result.get("Contractor Smith").intValue());
    }

}
