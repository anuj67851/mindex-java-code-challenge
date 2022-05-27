package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureUrl;

    @Autowired
    private ReportingStructureService reportingStructureService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{employeeId}";
    }

    @Test
    public void testRead() {
        Employee emp1 = new Employee();
        emp1.setEmployeeId("62c1084e-6e34-4630-93fd-9153afb65309");
        emp1.setDepartment("Engineering");
        emp1.setFirstName("Pete");
        emp1.setLastName("Best");
        emp1.setPosition("Developer II");


        Employee emp2 = new Employee();
        emp2.setEmployeeId("c0c2293d-16bd-4603-8e08-638a9d18b22c");
        emp2.setDepartment("Engineering");
        emp2.setFirstName("George");
        emp2.setLastName("Harrison");
        emp2.setPosition("Developer III");

        List<Employee> directReport = new ArrayList<>();
        directReport.add(emp1);
        directReport.add(emp2);

        Employee employee = new Employee();
        employee.setDirectReports(directReport);
        employee.setEmployeeId("03aa1462-ffa9-4978-901b-7c001562cf6f");
        employee.setDepartment("Engineering");
        employee.setFirstName("Ringo");
        employee.setLastName("Starr");
        employee.setPosition("Developer V");

        ReportingStructure reportingStructure = new ReportingStructure(employee, 2);

        // Read reporting structure checks
        ReportingStructure readReportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, reportingStructure.getEmployee().getEmployeeId()).getBody();
        assert readReportingStructure != null;
        assertReportingStructureEquivalence(reportingStructure, readReportingStructure);
    }

    private static void assertReportingStructureEquivalence(ReportingStructure expected, ReportingStructure actual) {
        assertEquals(expected.getNumberOfReports(), actual.getNumberOfReports());
        assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
    }

    // Method that checks employee equivalence
    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
        // Iterate over the direct reports and check each employee one by one
        if (expected.getDirectReports() != null){
            for(int i = 0; i < expected.getDirectReports().size(); i++){
                assertEmployeeEquivalence(expected.getDirectReports().get(i), actual.getDirectReports().get(i));
            }
        } else {
            assertNull(actual.getDirectReports());
        }
    }
}
