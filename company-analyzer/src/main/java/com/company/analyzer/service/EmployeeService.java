package com.company.analyzer.service;

import com.company.analyzer.model.Employee;

import java.util.*;

public class EmployeeService {
    private final Map<String, Employee> employeeMap = new HashMap<>();
    private Employee root;

    /**
     * Adds an employee to the internal employee map.
     *
     * @param emp the Employee object to be added.
     *            The employee's ID is used as the key in the map.
     *            If an employee with the same ID already exists, it will be overwritten.
     */
    public void addEmployee(Employee emp) {
        employeeMap.put(emp.getId(), emp);
    }

    /**
     * Builds the reporting hierarchy by linking each employee to their respective manager.
     *
     * @throws IllegalStateException if an employee references a manager ID that does not exist in the map.
     */
    public void buildHierarchy() {
        for (Employee employee : employeeMap.values()) {
            String managerId = employee.getManagerId();
            if (managerId == null) {
                root = employee; // No manager means ceo
            } else {
                Employee manager = employeeMap.get(managerId);
                if (manager != null) {
                    manager.getSubordinates().add(employee);
                } else {
                    throw new IllegalStateException("Manager with ID " + managerId + " not found.");
                }
            }
        }
    }

    /**
     * Analyzes manager salaries starting from the given root employee and returns a report.
     *
     * @param root the root employee (typically the CEO)
     * return list of formatted salary analysis strings for each manager
     */
    public List<String> analyzeManagerSalaries(Employee root) {
        List<String> report = new ArrayList<>();
        analyzeSalaryHelper(root, report);
        return report;
    }


    /**
     * Recursively analyzes whether a manager's salary is within an expected range
     * compared to the average salary of their direct subordinates. Adds a report
     * entry if the salary deviates beyond defined thresholds.
     *
     * @param root the manager to analyze
     * @param report the list to collect salary analysis messages
     */
    private void analyzeSalaryHelper(Employee root, List<String> report) {
        if (root.getSubordinates().isEmpty()) return;

        double total = 0.0;
        for (Employee e : root.getSubordinates()) {
            total += e.getSalary();
        }

        double avg = total / root.getSubordinates().size();
        double salary = root.getSalary();

        if (salary < 1.2 * avg) {
            double deficit = (1.2 * avg) - salary;
            report.add(String.format("Manager %s earns %.2f LESS than expected (should be at least %.2f)",
                    root.getFullName(), deficit, 1.2 * avg));
        } else if (salary > 1.5 * avg) {
            double excess = salary - (1.5 * avg);
            report.add(String.format("Manager %s earns %.2f MORE than expected (should be at most %.2f)",
                    root.getFullName(), excess, 1.5 * avg));
        }

        for (Employee e : root.getSubordinates()) {
            analyzeSalaryHelper(e, report);
        }
    }

    /**
     * Builds a map of each employee's full name to their reporting line depth,
     * starting from the given root employee.
     *
     * @param root the top-level employee (e.g., CEO)
     * @param currentDepth the starting depth (usually 0)
     * @return a map of employee names to their corresponding depth in the hierarchy
     */
    public Map<String, Integer> analyzeReportingLineDepth(Employee root, int currentDepth) {
        Map<String, Integer> depthMap = new LinkedHashMap<>();
        analyzeReportingLineDepthHelper(root, currentDepth, depthMap);
        return depthMap;
    }

    /**
     * Recursively traverses the employee hierarchy to record employees whose
     * reporting line depth exceeds 4 levels.
     *
     * @param root the current employee node in the hierarchy
     * @param currentDepth the depth of the current employee in the reporting chain
     * @param depthMap a map to store employees exceeding depth threshold along with their depth
     */
    private void analyzeReportingLineDepthHelper(Employee root, int currentDepth, Map<String, Integer> depthMap) {
        if (currentDepth > 4) {
            depthMap.put(root.getFullName(), currentDepth);
        }

        for (Employee e : root.getSubordinates()) {
            analyzeReportingLineDepthHelper(e, currentDepth + 1, depthMap);
        }
    }

    public Employee getRoot() {
        return root;
    }

    public Map<String, Employee> getAllEmployees() {
        return employeeMap;
    }
}
