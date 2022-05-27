package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Creating employee Reporting Structure with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        // if the employee has no direct reports
        if (employee.getDirectReports() == null){
            return new ReportingStructure(employee, 0);
        }

        Employee initializedEmployee = initializeEmployeeDirectReports(employee);
        // Return a new Reporting Structure object with employee and the size of reporting employees
        return new ReportingStructure(initializedEmployee, countDirectReports(initializedEmployee));
    }

    /**
     * Method that recursively initialized the employees in the direct reports to create an employee tree
     **/
    private Employee initializeEmployeeDirectReports(Employee employee) {
        if (employee.getDirectReports() != null) {
            // For each employee, fetch its corresponding entry from employee repo and create a list
            List<Employee> initializedEmp = employee.getDirectReports().stream().map(temp -> employeeRepository.findByEmployeeId(temp.getEmployeeId())).collect(Collectors.toList());

            // Perform same operations for each employee recursively
            for (int i = 0; i < initializedEmp.size(); i++) {
                initializedEmp.set(i, initializeEmployeeDirectReports(initializedEmp.get(i)));
            }

            employee.setDirectReports(initializedEmp);
        }
        return employee;
    }

    /**
     * Method that counts the number of distinct employees under current employee
     **/
    private int countDirectReports(Employee employee){
        // List that contains all the reachable employees
        List<Employee> employeeList = new ArrayList<>();

        // Performing a bfs to find all the distinct employees on the reachable nodes
        Queue<Employee> employeeQueue = new ArrayDeque<>(employee.getDirectReports());
        Employee temp;
        while (!employeeQueue.isEmpty()){
            temp = employeeQueue.poll();
            if (!employeeList.contains(temp)){
                employeeList.add(temp);
            }
            if (temp.getDirectReports() != null){
                employeeQueue.addAll(temp.getDirectReports());
            }
        }
        return employeeList.size();
    }
}
