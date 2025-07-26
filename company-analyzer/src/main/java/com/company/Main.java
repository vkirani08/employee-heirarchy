package com.company;

import com.company.analyzer.model.Employee;
import com.company.analyzer.service.EmployeeService;
import com.company.analyzer.util.EmployeeCSVLoader;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        EmployeeService service = new EmployeeService();
        EmployeeCSVLoader loader = new EmployeeCSVLoader(service);

        try {
            loader.loadFromReader(new FileReader("src/main/resources/employees.csv"));
            Employee root = service.getRoot();

            System.out.println("Root: " + root.getFullName());

            System.out.println("\n--- Salary Analysis ---");
            List<String> salaryReport = service.analyzeManagerSalaries(root);

            System.out.println("\n--- Reporting Line Analysis ---");
            Map<String, Integer> depthMap = service.analyzeReportingLineDepth(root, 0);

            for (Employee e : root.getSubordinates()) {
                System.out.println(e.getFullName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}