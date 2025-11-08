package com.anr.localmdb.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Comprehensive test suite for Policy model class
 * Target: >90% code coverage
 * 
 * @author amitr
 */
public class PolicyTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // ========================================================================
    // CONSTRUCTOR AND BASIC TESTS
    // ========================================================================

    @Test
    void testPolicyCreation() {
        // Act
        Policy policy = new Policy();
        
        // Assert
        assertNotNull(policy);
        assertNull(policy.getPolicyID());
        assertNull(policy.getHccID());
        assertNull(policy.getGroup());
        assertNull(policy.getDivision());
        assertNull(policy.getPolicyStartDate());
        assertNull(policy.getPolicyExpiryDate());
        assertNull(policy.getPlans());
    }

    // ========================================================================
    // GETTER AND SETTER TESTS
    // ========================================================================

    @Test
    void testSetAndGetPolicyID() {
        // Arrange
        Policy policy = new Policy();
        String policyId = "POL-2024-001";
        
        // Act
        policy.setPolicyID(policyId);
        
        // Assert
        assertEquals(policyId, policy.getPolicyID());
    }

    @Test
    void testSetAndGetHccID() {
        // Arrange
        Policy policy = new Policy();
        String hccId = "HCC-12345";
        
        // Act
        policy.setHccID(hccId);
        
        // Assert
        assertEquals(hccId, policy.getHccID());
    }

    @Test
    void testSetAndGetGroup() {
        // Arrange
        Policy policy = new Policy();
        String group = "Corporate Group A";
        
        // Act
        policy.setGroup(group);
        
        // Assert
        assertEquals(group, policy.getGroup());
    }

    @Test
    void testSetAndGetDivision() {
        // Arrange
        Policy policy = new Policy();
        String division = "North Division";
        
        // Act
        policy.setDivision(division);
        
        // Assert
        assertEquals(division, policy.getDivision());
    }

    @Test
    void testSetAndGetPolicyStartDate() throws ParseException {
        // Arrange
        Policy policy = new Policy();
        Date startDate = DATE_FORMAT.parse("2024-01-01");
        
        // Act
        policy.setPolicyStartDate(startDate);
        
        // Assert
        assertEquals(startDate, policy.getPolicyStartDate());
    }

    @Test
    void testSetAndGetPolicyExpiryDate() throws ParseException {
        // Arrange
        Policy policy = new Policy();
        Date expiryDate = DATE_FORMAT.parse("2024-12-31");
        
        // Act
        policy.setPolicyExpiryDate(expiryDate);
        
        // Assert
        assertEquals(expiryDate, policy.getPolicyExpiryDate());
    }

    @Test
    void testSetAndGetPlans() {
        // Arrange
        Policy policy = new Policy();
        Plan plan1 = new Plan();
        plan1.setPlanName("Plan 1");
        Plan plan2 = new Plan();
        plan2.setPlanName("Plan 2");
        List<Plan> plans = Arrays.asList(plan1, plan2);
        
        // Act
        policy.setPlans(plans);
        
        // Assert
        assertEquals(plans, policy.getPlans());
        assertEquals(2, policy.getPlans().size());
    }

    // ========================================================================
    // NULL VALUE TESTS
    // ========================================================================

    @Test
    void testSetNullValues() throws ParseException {
        // Arrange
        Policy policy = new Policy();
        policy.setPolicyID("POL-001");
        policy.setHccID("HCC-001");
        policy.setGroup("Group A");
        policy.setDivision("Division 1");
        policy.setPolicyStartDate(DATE_FORMAT.parse("2024-01-01"));
        policy.setPolicyExpiryDate(DATE_FORMAT.parse("2024-12-31"));
        policy.setPlans(new ArrayList<>());
        
        // Act - Set all to null
        policy.setPolicyID(null);
        policy.setHccID(null);
        policy.setGroup(null);
        policy.setDivision(null);
        policy.setPolicyStartDate(null);
        policy.setPolicyExpiryDate(null);
        policy.setPlans(null);
        
        // Assert
        assertNull(policy.getPolicyID());
        assertNull(policy.getHccID());
        assertNull(policy.getGroup());
        assertNull(policy.getDivision());
        assertNull(policy.getPolicyStartDate());
        assertNull(policy.getPolicyExpiryDate());
        assertNull(policy.getPlans());
    }

    // ========================================================================
    // TO STRING TESTS
    // ========================================================================

    @Test
    void testToString_withHccID() {
        // Arrange
        Policy policy = new Policy();
        policy.setHccID("HCC-12345");
        
        // Act
        String result = policy.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("HCC-12345"));
        assertTrue(result.contains("Policy"));
    }

    @Test
    void testToString_withNullHccID() {
        // Arrange
        Policy policy = new Policy();
        
        // Act
        String result = policy.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Policy"));
    }

    @Test
    void testToString_format() {
        // Arrange
        Policy policy = new Policy();
        policy.setHccID("HCC-TEST");
        
        // Act
        String result = policy.toString();
        
        // Assert
        assertEquals("Policy [hccID=HCC-TEST]", result);
    }

    // ========================================================================
    // DATE HANDLING TESTS
    // ========================================================================

    @Test
    void testDateRange() throws ParseException {
        // Arrange
        Policy policy = new Policy();
        Date startDate = DATE_FORMAT.parse("2024-01-01");
        Date endDate = DATE_FORMAT.parse("2024-12-31");
        
        // Act
        policy.setPolicyStartDate(startDate);
        policy.setPolicyExpiryDate(endDate);
        
        // Assert
        assertEquals(startDate, policy.getPolicyStartDate());
        assertEquals(endDate, policy.getPolicyExpiryDate());
        assertTrue(policy.getPolicyExpiryDate().after(policy.getPolicyStartDate()));
    }

    @Test
    void testSameDateForStartAndExpiry() throws ParseException {
        // Arrange
        Policy policy = new Policy();
        Date date = DATE_FORMAT.parse("2024-06-15");
        
        // Act
        policy.setPolicyStartDate(date);
        policy.setPolicyExpiryDate(date);
        
        // Assert
        assertEquals(date, policy.getPolicyStartDate());
        assertEquals(date, policy.getPolicyExpiryDate());
    }

    @Test
    void testFutureDates() throws ParseException {
        // Arrange
        Policy policy = new Policy();
        Date futureDate = DATE_FORMAT.parse("2030-12-31");
        
        // Act
        policy.setPolicyStartDate(futureDate);
        policy.setPolicyExpiryDate(futureDate);
        
        // Assert
        assertEquals(futureDate, policy.getPolicyStartDate());
        assertEquals(futureDate, policy.getPolicyExpiryDate());
    }

    @Test
    void testPastDates() throws ParseException {
        // Arrange
        Policy policy = new Policy();
        Date pastDate = DATE_FORMAT.parse("2020-01-01");
        
        // Act
        policy.setPolicyStartDate(pastDate);
        policy.setPolicyExpiryDate(pastDate);
        
        // Assert
        assertEquals(pastDate, policy.getPolicyStartDate());
        assertEquals(pastDate, policy.getPolicyExpiryDate());
    }

    // ========================================================================
    // PLANS COLLECTION TESTS
    // ========================================================================

    @Test
    void testEmptyPlansList() {
        // Arrange
        Policy policy = new Policy();
        List<Plan> emptyPlans = new ArrayList<>();
        
        // Act
        policy.setPlans(emptyPlans);
        
        // Assert
        assertNotNull(policy.getPlans());
        assertTrue(policy.getPlans().isEmpty());
    }

    @Test
    void testSinglePlan() {
        // Arrange
        Policy policy = new Policy();
        Plan plan = new Plan();
        plan.setPlanName("Single Plan");
        List<Plan> plans = Arrays.asList(plan);
        
        // Act
        policy.setPlans(plans);
        
        // Assert
        assertEquals(1, policy.getPlans().size());
        assertEquals("Single Plan", policy.getPlans().get(0).getPlanName());
    }

    @Test
    void testMultiplePlans() {
        // Arrange
        Policy policy = new Policy();
        Plan plan1 = new Plan();
        plan1.setPlanName("Medical Plan");
        Plan plan2 = new Plan();
        plan2.setPlanName("Dental Plan");
        Plan plan3 = new Plan();
        plan3.setPlanName("Vision Plan");
        List<Plan> plans = Arrays.asList(plan1, plan2, plan3);
        
        // Act
        policy.setPlans(plans);
        
        // Assert
        assertEquals(3, policy.getPlans().size());
        assertEquals("Medical Plan", policy.getPlans().get(0).getPlanName());
        assertEquals("Dental Plan", policy.getPlans().get(1).getPlanName());
        assertEquals("Vision Plan", policy.getPlans().get(2).getPlanName());
    }

    // ========================================================================
    // JSON SERIALIZATION TESTS
    // ========================================================================

    @Test
    void testJsonSerialization_withAllFields() throws JsonProcessingException, ParseException {
        // Arrange
        Policy policy = new Policy();
        policy.setPolicyID("POL-001");
        policy.setHccID("HCC-001");
        policy.setGroup("Group A");
        policy.setDivision("Division 1");
        policy.setPolicyStartDate(DATE_FORMAT.parse("2024-01-01"));
        policy.setPolicyExpiryDate(DATE_FORMAT.parse("2024-12-31"));
        
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        String json = mapper.writeValueAsString(policy);
        
        // Assert
        assertNotNull(json);
        assertTrue(json.contains("POL-001"));
        assertTrue(json.contains("HCC-001"));
    }

    @Test
    void testJsonSerialization_withNullFields() throws JsonProcessingException {
        // Arrange
        Policy policy = new Policy();
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        String json = mapper.writeValueAsString(policy);
        
        // Assert
        assertNotNull(json);
        assertEquals("{}", json);
    }

    // ========================================================================
    // INTEGRATION TESTS
    // ========================================================================

    @Test
    void testCompleteWorkflow() throws ParseException {
        // Arrange & Act
        Policy policy = new Policy();
        policy.setPolicyID("POL-2024-12345");
        policy.setHccID("HCC-CORP-001");
        policy.setGroup("Corporate Group");
        policy.setDivision("Technology Division");
        policy.setPolicyStartDate(DATE_FORMAT.parse("2024-01-01"));
        policy.setPolicyExpiryDate(DATE_FORMAT.parse("2024-12-31"));
        
        Plan plan1 = new Plan();
        plan1.setPlanName("Premium Medical");
        Plan plan2 = new Plan();
        plan2.setPlanName("Basic Dental");
        policy.setPlans(Arrays.asList(plan1, plan2));
        
        // Assert
        assertEquals("POL-2024-12345", policy.getPolicyID());
        assertEquals("HCC-CORP-001", policy.getHccID());
        assertEquals("Corporate Group", policy.getGroup());
        assertEquals("Technology Division", policy.getDivision());
        assertNotNull(policy.getPolicyStartDate());
        assertNotNull(policy.getPolicyExpiryDate());
        assertEquals(2, policy.getPlans().size());
        
        String toString = policy.toString();
        assertTrue(toString.contains("HCC-CORP-001"));
    }

    @Test
    void testEmptyStringValues() {
        // Arrange
        Policy policy = new Policy();
        
        // Act
        policy.setPolicyID("");
        policy.setHccID("");
        policy.setGroup("");
        policy.setDivision("");
        
        // Assert
        assertEquals("", policy.getPolicyID());
        assertEquals("", policy.getHccID());
        assertEquals("", policy.getGroup());
        assertEquals("", policy.getDivision());
    }

    @Test
    void testLongStringValues() {
        // Arrange
        Policy policy = new Policy();
        String longId = "A".repeat(50);
        
        // Act
        policy.setPolicyID(longId);
        policy.setHccID(longId);
        
        // Assert
        assertEquals(longId, policy.getPolicyID());
        assertEquals(longId, policy.getHccID());
    }

    @Test
    void testSpecialCharactersInStrings() {
        // Arrange
        Policy policy = new Policy();
        String specialChars = "Policy @#$%^&*()";
        
        // Act
        policy.setGroup(specialChars);
        
        // Assert
        assertEquals(specialChars, policy.getGroup());
    }

    @Test
    void testPolicyWithComplexPlanHierarchy() {
        // Arrange
        Policy policy = new Policy();
        policy.setPolicyID("POL-COMPLEX");
        
        List<Plan> plans = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Plan plan = new Plan();
            plan.setPlanName("Plan " + i);
            plan.setPlanNumber("PLN-00" + i);
            plan.setStatus("Active");
            plans.add(plan);
        }
        
        // Act
        policy.setPlans(plans);
        
        // Assert
        assertEquals(5, policy.getPlans().size());
        assertEquals("Plan 1", policy.getPlans().get(0).getPlanName());
        assertEquals("Plan 5", policy.getPlans().get(4).getPlanName());
    }
}
