package com.anr.localmdb.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Comprehensive test suite for Plan model class
 * Target: >90% code coverage
 * 
 * @author amitr
 */
public class PlanTest {

    // ========================================================================
    // CONSTRUCTOR AND BASIC TESTS
    // ========================================================================

    @Test
    void testPlanCreation() {
        // Act
        Plan plan = new Plan();
        
        // Assert
        assertNotNull(plan);
        assertNull(plan.getId());
        assertNull(plan.getPlanDefinitionId());
        assertNull(plan.getPlanName());
        assertNull(plan.getPlanNumber());
        assertNull(plan.getSegment());
        assertNull(plan.getPlanFamily());
        assertNull(plan.getCoverageType());
        assertNull(plan.getStatus());
    }

    // ========================================================================
    // GETTER AND SETTER TESTS
    // ========================================================================

    @Test
    void testSetAndGetId() {
        // Arrange
        Plan plan = new Plan();
        Long id = 12345L;
        
        // Act
        plan.setId(id);
        
        // Assert
        assertEquals(id, plan.getId());
    }

    @Test
    void testSetAndGetPlanDefinitionId() {
        // Arrange
        Plan plan = new Plan();
        Integer planDefId = 100;
        
        // Act
        plan.setPlanDefinitionId(planDefId);
        
        // Assert
        assertEquals(planDefId, plan.getPlanDefinitionId());
    }

    @Test
    void testSetAndGetPlanName() {
        // Arrange
        Plan plan = new Plan();
        String planName = "Premium Health Plan";
        
        // Act
        plan.setPlanName(planName);
        
        // Assert
        assertEquals(planName, plan.getPlanName());
    }

    @Test
    void testSetAndGetPlanNumber() {
        // Arrange
        Plan plan = new Plan();
        String planNumber = "PLN-2024-001";
        
        // Act
        plan.setPlanNumber(planNumber);
        
        // Assert
        assertEquals(planNumber, plan.getPlanNumber());
    }

    @Test
    void testSetAndGetSegment() {
        // Arrange
        Plan plan = new Plan();
        String segment = "Individual";
        
        // Act
        plan.setSegment(segment);
        
        // Assert
        assertEquals(segment, plan.getSegment());
    }

    @Test
    void testSetAndGetPlanFamily() {
        // Arrange
        Plan plan = new Plan();
        String planFamily = "Gold Family";
        
        // Act
        plan.setPlanFamily(planFamily);
        
        // Assert
        assertEquals(planFamily, plan.getPlanFamily());
    }

    @Test
    void testSetAndGetCoverageType() {
        // Arrange
        Plan plan = new Plan();
        String coverageType = "Medical";
        
        // Act
        plan.setCoverageType(coverageType);
        
        // Assert
        assertEquals(coverageType, plan.getCoverageType());
    }

    @Test
    void testSetAndGetStatus() {
        // Arrange
        Plan plan = new Plan();
        String status = "Active";
        
        // Act
        plan.setStatus(status);
        
        // Assert
        assertEquals(status, plan.getStatus());
    }

    // ========================================================================
    // NULL VALUE TESTS
    // ========================================================================

    @Test
    void testSetNullValues() {
        // Arrange
        Plan plan = new Plan();
        plan.setId(123L);
        plan.setPlanDefinitionId(100);
        plan.setPlanName("Test Plan");
        plan.setPlanNumber("PLN-001");
        plan.setSegment("Individual");
        plan.setPlanFamily("Gold");
        plan.setCoverageType("Medical");
        plan.setStatus("Active");
        
        // Act - Set all to null
        plan.setId(null);
        plan.setPlanDefinitionId(null);
        plan.setPlanName(null);
        plan.setPlanNumber(null);
        plan.setSegment(null);
        plan.setPlanFamily(null);
        plan.setCoverageType(null);
        plan.setStatus(null);
        
        // Assert
        assertNull(plan.getId());
        assertNull(plan.getPlanDefinitionId());
        assertNull(plan.getPlanName());
        assertNull(plan.getPlanNumber());
        assertNull(plan.getSegment());
        assertNull(plan.getPlanFamily());
        assertNull(plan.getCoverageType());
        assertNull(plan.getStatus());
    }

    // ========================================================================
    // TO STRING TESTS
    // ========================================================================

    @Test
    void testToString_withAllFields() {
        // Arrange
        Plan plan = new Plan();
        plan.setPlanDefinitionId(100);
        plan.setPlanName("Premium Plan");
        plan.setPlanNumber("PLN-001");
        
        // Act
        String result = plan.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("100"));
        assertTrue(result.contains("Premium Plan"));
        assertTrue(result.contains("PLN-001"));
        assertTrue(result.contains("Plan"));
    }

    @Test
    void testToString_withNullFields() {
        // Arrange
        Plan plan = new Plan();
        
        // Act
        String result = plan.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Plan"));
    }

    @Test
    void testToString_format() {
        // Arrange
        Plan plan = new Plan();
        plan.setPlanDefinitionId(123);
        plan.setPlanName("Test Plan");
        plan.setPlanNumber("PLN-123");
        
        // Act
        String result = plan.toString();
        
        // Assert
        assertEquals("Plan [planDefinitionId=123, planName=Test Plan, planNumber=PLN-123]", result);
    }

    // ========================================================================
    // JSON SERIALIZATION TESTS
    // ========================================================================

    @Test
    void testJsonSerialization_withAllFields() throws JsonProcessingException {
        // Arrange
        Plan plan = new Plan();
        plan.setId(1L);
        plan.setPlanDefinitionId(100);
        plan.setPlanName("Premium Plan");
        plan.setPlanNumber("PLN-001");
        plan.setSegment("Individual");
        plan.setPlanFamily("Gold");
        plan.setCoverageType("Medical");
        plan.setStatus("Active");
        
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        String json = mapper.writeValueAsString(plan);
        
        // Assert
        assertNotNull(json);
        assertTrue(json.contains("Premium Plan"));
        assertTrue(json.contains("PLN-001"));
    }

    @Test
    void testJsonSerialization_withNullFields() throws JsonProcessingException {
        // Arrange
        Plan plan = new Plan();
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        String json = mapper.writeValueAsString(plan);
        
        // Assert
        assertNotNull(json);
        // JsonInclude(Include.NON_NULL) should exclude null fields
        assertEquals("{}", json);
    }

    @Test
    void testJsonDeserialization() throws JsonProcessingException {
        // Arrange
        String json = "{\"id\":1,\"planDefinitionId\":100,\"planName\":\"Test Plan\",\"planNumber\":\"PLN-001\",\"segment\":\"Individual\",\"planFamily\":\"Gold\",\"coverageType\":\"Medical\",\"status\":\"Active\"}";
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        Plan plan = mapper.readValue(json, Plan.class);
        
        // Assert
        assertNotNull(plan);
        assertEquals(1L, plan.getId());
        assertEquals(100, plan.getPlanDefinitionId());
        assertEquals("Test Plan", plan.getPlanName());
        assertEquals("PLN-001", plan.getPlanNumber());
        assertEquals("Individual", plan.getSegment());
        assertEquals("Gold", plan.getPlanFamily());
        assertEquals("Medical", plan.getCoverageType());
        assertEquals("Active", plan.getStatus());
    }

    // ========================================================================
    // INTEGRATION TESTS
    // ========================================================================

    @Test
    void testCompleteWorkflow() {
        // Arrange & Act
        Plan plan = new Plan();
        plan.setId(1L);
        plan.setPlanDefinitionId(200);
        plan.setPlanName("Comprehensive Health Plan");
        plan.setPlanNumber("PLN-2024-200");
        plan.setSegment("Family");
        plan.setPlanFamily("Platinum");
        plan.setCoverageType("Medical & Dental");
        plan.setStatus("Active");
        
        // Assert
        assertEquals(1L, plan.getId());
        assertEquals(200, plan.getPlanDefinitionId());
        assertEquals("Comprehensive Health Plan", plan.getPlanName());
        assertEquals("PLN-2024-200", plan.getPlanNumber());
        assertEquals("Family", plan.getSegment());
        assertEquals("Platinum", plan.getPlanFamily());
        assertEquals("Medical & Dental", plan.getCoverageType());
        assertEquals("Active", plan.getStatus());
        
        String toString = plan.toString();
        assertTrue(toString.contains("200"));
        assertTrue(toString.contains("Comprehensive Health Plan"));
    }

    @Test
    void testEmptyStringValues() {
        // Arrange
        Plan plan = new Plan();
        
        // Act
        plan.setPlanName("");
        plan.setPlanNumber("");
        plan.setSegment("");
        plan.setPlanFamily("");
        plan.setCoverageType("");
        plan.setStatus("");
        
        // Assert
        assertEquals("", plan.getPlanName());
        assertEquals("", plan.getPlanNumber());
        assertEquals("", plan.getSegment());
        assertEquals("", plan.getPlanFamily());
        assertEquals("", plan.getCoverageType());
        assertEquals("", plan.getStatus());
    }

    @Test
    void testLongStringValues() {
        // Arrange
        Plan plan = new Plan();
        String longName = "A".repeat(100);
        String longNumber = "B".repeat(50);
        
        // Act
        plan.setPlanName(longName);
        plan.setPlanNumber(longNumber);
        
        // Assert
        assertEquals(longName, plan.getPlanName());
        assertEquals(longNumber, plan.getPlanNumber());
    }

    @Test
    void testSpecialCharactersInStrings() {
        // Arrange
        Plan plan = new Plan();
        String specialChars = "Plan @#$%^&*()";
        
        // Act
        plan.setPlanName(specialChars);
        
        // Assert
        assertEquals(specialChars, plan.getPlanName());
    }

    @Test
    void testVariousStatusValues() {
        // Test different status values
        String[] statuses = {"Active", "Inactive", "Pending", "Expired", "Suspended"};
        
        for (String status : statuses) {
            Plan plan = new Plan();
            plan.setStatus(status);
            assertEquals(status, plan.getStatus());
        }
    }

    @Test
    void testVariousCoverageTypes() {
        // Test different coverage types
        String[] coverageTypes = {"Medical", "Dental", "Vision", "Medical & Dental", "Comprehensive"};
        
        for (String coverageType : coverageTypes) {
            Plan plan = new Plan();
            plan.setCoverageType(coverageType);
            assertEquals(coverageType, plan.getCoverageType());
        }
    }
}
