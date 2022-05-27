package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationFetchUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationFetchUrl = "http://localhost:" + port + "/compensation/{empId}";
    }

    // Method to create a date object based on the string passed
    private Date parseDate(String dateString){
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    @Test
    public void testCreateRead() {

        // Id of John Lennon
        Compensation compensation = new Compensation();
        compensation.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        compensation.setSalary(22.0f);
        compensation.setEffectiveDate(parseDate("02-21-2023"));

        // Create checks
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();

        assert createdCompensation != null;
        assertNotNull(createdCompensation.getEmployeeId());
        assertCompensationEquivalence(compensation, createdCompensation);


        // Read checks
        Compensation readCompensation = restTemplate.getForEntity(compensationFetchUrl, Compensation.class, createdCompensation.getEmployeeId()).getBody();
        assert readCompensation != null;
        assertEquals(createdCompensation.getEmployeeId(), readCompensation.getEmployeeId());
        assertCompensationEquivalence(createdCompensation, readCompensation);
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary(), 0.0f);
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
}
