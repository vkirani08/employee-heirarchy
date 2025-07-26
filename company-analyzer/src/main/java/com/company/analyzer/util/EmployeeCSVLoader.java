package com.company.analyzer.util;

import com.company.analyzer.model.Employee;
import com.company.analyzer.service.EmployeeService;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;

public class EmployeeCSVLoader {
    private final EmployeeService employeeService;

    public EmployeeCSVLoader(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Loads employee data from a CSV-formatted Reader, skipping the header row.
     * Each line must contain: id, first name, last name, salary, and manager ID.
     * After loading, builds the employee hierarchy.
     *
     * @param reader the source of CSV data
     * @throws IOException if an I/O error occurs during reading
     * @throws IllegalArgumentException if any line is malformed
     */
    public void loadFromReader(Reader reader) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] tokens = line.split(",", -1);
                if (tokens.length < 5) {
                    throw new IllegalArgumentException("Invalid line: " + line);
                }

                String id = tokens[0].trim();
                String firstName = tokens[1].trim();
                String lastName = tokens[2].trim();
                int salary = Integer.parseInt(tokens[3].trim());
                String managerId = tokens[4].trim();

                Employee emp = new Employee(id, firstName, lastName, salary, managerId);
                employeeService.addEmployee(emp);
            }

            employeeService.buildHierarchy();
        }
    }

}
