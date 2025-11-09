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
 * Comprehensive test suite for InsuranceMember model class
 * Target: >90% code coverage
 * 
 * @author amitr
 */
public class InsuranceMemberTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // ========================================================================
    // CONSTRUCTOR AND BASIC TESTS
    // ========================================================================

    @Test
    void testInsuranceMemberCreation() {
        // Act
        InsuranceMember member = new InsuranceMember();
        
        // Assert
        assertNotNull(member);
        assertNull(member.getId());
        assertNull(member.getPartyId());
        assertNull(member.getFirstname());
        assertNull(member.getLastname());
        assertNull(member.getDateOfBirth());
        assertNull(member.getPolicies());
    }

    // ========================================================================
    // GETTER AND SETTER TESTS
    // ========================================================================

    @Test
    void testSetAndGetId() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        String id = "MEM-12345";
        
        // Act
        member.setId(id);
        
        // Assert
        assertEquals(id, member.getId());
    }

    @Test
    void testSetAndGetPartyId() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        String partyId = "PARTY-67890";
        
        // Act
        member.setPartyId(partyId);
        
        // Assert
        assertEquals(partyId, member.getPartyId());
    }

    @Test
    void testSetAndGetFirstname() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        String firstname = "John";
        
        // Act
        member.setFirstname(firstname);
        
        // Assert
        assertEquals(firstname, member.getFirstname());
    }

    @Test
    void testSetAndGetLastname() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        String lastname = "Doe";
        
        // Act
        member.setLastname(lastname);
        
        // Assert
        assertEquals(lastname, member.getLastname());
    }

    @Test
    void testSetAndGetDateOfBirth() throws ParseException {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        Date dob = DATE_FORMAT.parse("1990-05-15");
        
        // Act
        member.setDateOfBirth(dob);
        
        // Assert
        assertEquals(dob, member.getDateOfBirth());
    }

    @Test
    void testSetAndGetPolicies() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        Policy policy1 = new Policy();
        policy1.setPolicyID("POL-001");
        Policy policy2 = new Policy();
        policy2.setPolicyID("POL-002");
        List<Policy> policies = Arrays.asList(policy1, policy2);
        
        // Act
        member.setPolicies(policies);
        
        // Assert
        assertEquals(policies, member.getPolicies());
        assertEquals(2, member.getPolicies().size());
    }

    // ========================================================================
    // NULL VALUE TESTS
    // ========================================================================

    @Test
    void testSetNullValues() throws ParseException {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        member.setId("MEM-001");
        member.setPartyId("PARTY-001");
        member.setFirstname("John");
        member.setLastname("Doe");
        member.setDateOfBirth(DATE_FORMAT.parse("1990-01-01"));
        member.setPolicies(new ArrayList<>());
        
        // Act - Set all to null
        member.setId(null);
        member.setPartyId(null);
        member.setFirstname(null);
        member.setLastname(null);
        member.setDateOfBirth(null);
        member.setPolicies(null);
        
        // Assert
        assertNull(member.getId());
        assertNull(member.getPartyId());
        assertNull(member.getFirstname());
        assertNull(member.getLastname());
        assertNull(member.getDateOfBirth());
        assertNull(member.getPolicies());
    }

    // ========================================================================
    // TO STRING TESTS
    // ========================================================================

    @Test
    void testToString_withAllFields() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        member.setPartyId("PARTY-123");
        member.setFirstname("Jane");
        member.setLastname("Smith");
        
        // Act
        String result = member.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("PARTY-123"));
        assertTrue(result.contains("Jane"));
        assertTrue(result.contains("Smith"));
        assertTrue(result.contains("InsuranceMember"));
    }

    @Test
    void testToString_withNullFields() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        
        // Act
        String result = member.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("InsuranceMember"));
    }

    @Test
    void testToString_format() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        member.setPartyId("PARTY-TEST");
        member.setFirstname("Test");
        member.setLastname("User");
        
        // Act
        String result = member.toString();
        
        // Assert
        assertEquals("InsuranceMember [partyId=PARTY-TEST, firstname=Test, lastname=User]", result);
    }

    // ========================================================================
    // DATE OF BIRTH TESTS
    // ========================================================================

    @Test
    void testVariousDateOfBirth() throws ParseException {
        // Test different date of birth scenarios
        String[] dates = {
            "1950-01-01",  // Older person
            "1990-06-15",  // Middle-aged
            "2010-12-31",  // Younger person
            "2000-02-29"   // Leap year
        };
        
        for (String dateStr : dates) {
            InsuranceMember member = new InsuranceMember();
            Date dob = DATE_FORMAT.parse(dateStr);
            member.setDateOfBirth(dob);
            assertEquals(dob, member.getDateOfBirth());
        }
    }

    @Test
    void testFutureDateOfBirth() throws ParseException {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        Date futureDate = DATE_FORMAT.parse("2030-01-01");
        
        // Act
        member.setDateOfBirth(futureDate);
        
        // Assert
        assertEquals(futureDate, member.getDateOfBirth());
    }

    @Test
    void testVeryOldDateOfBirth() throws ParseException {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        Date oldDate = DATE_FORMAT.parse("1900-01-01");
        
        // Act
        member.setDateOfBirth(oldDate);
        
        // Assert
        assertEquals(oldDate, member.getDateOfBirth());
    }

    // ========================================================================
    // POLICIES COLLECTION TESTS
    // ========================================================================

    @Test
    void testEmptyPoliciesList() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        List<Policy> emptyPolicies = new ArrayList<>();
        
        // Act
        member.setPolicies(emptyPolicies);
        
        // Assert
        assertNotNull(member.getPolicies());
        assertTrue(member.getPolicies().isEmpty());
    }

    @Test
    void testSinglePolicy() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        Policy policy = new Policy();
        policy.setPolicyID("POL-SINGLE");
        List<Policy> policies = Arrays.asList(policy);
        
        // Act
        member.setPolicies(policies);
        
        // Assert
        assertEquals(1, member.getPolicies().size());
        assertEquals("POL-SINGLE", member.getPolicies().get(0).getPolicyID());
    }

    @Test
    void testMultiplePolicies() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        Policy policy1 = new Policy();
        policy1.setPolicyID("POL-001");
        Policy policy2 = new Policy();
        policy2.setPolicyID("POL-002");
        Policy policy3 = new Policy();
        policy3.setPolicyID("POL-003");
        List<Policy> policies = Arrays.asList(policy1, policy2, policy3);
        
        // Act
        member.setPolicies(policies);
        
        // Assert
        assertEquals(3, member.getPolicies().size());
        assertEquals("POL-001", member.getPolicies().get(0).getPolicyID());
        assertEquals("POL-002", member.getPolicies().get(1).getPolicyID());
        assertEquals("POL-003", member.getPolicies().get(2).getPolicyID());
    }

    // ========================================================================
    // JSON SERIALIZATION TESTS
    // ========================================================================

    @Test
    void testJsonSerialization_withAllFields() throws JsonProcessingException, ParseException {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        member.setId("MEM-001");
        member.setPartyId("PARTY-001");
        member.setFirstname("John");
        member.setLastname("Doe");
        member.setDateOfBirth(DATE_FORMAT.parse("1990-01-01"));
        
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        String json = mapper.writeValueAsString(member);
        
        // Assert
        assertNotNull(json);
        assertTrue(json.contains("MEM-001"));
        assertTrue(json.contains("John"));
        assertTrue(json.contains("Doe"));
    }

    @Test
    void testJsonSerialization_withNullFields() throws JsonProcessingException {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        String json = mapper.writeValueAsString(member);
        
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
        InsuranceMember member = new InsuranceMember();
        member.setId("MEM-2024-001");
        member.setPartyId("PARTY-CORP-123");
        member.setFirstname("Alice");
        member.setLastname("Johnson");
        member.setDateOfBirth(DATE_FORMAT.parse("1985-03-20"));
        
        Policy policy1 = new Policy();
        policy1.setPolicyID("POL-HEALTH-001");
        policy1.setHccID("HCC-001");
        
        Policy policy2 = new Policy();
        policy2.setPolicyID("POL-DENTAL-001");
        policy2.setHccID("HCC-002");
        
        member.setPolicies(Arrays.asList(policy1, policy2));
        
        // Assert
        assertEquals("MEM-2024-001", member.getId());
        assertEquals("PARTY-CORP-123", member.getPartyId());
        assertEquals("Alice", member.getFirstname());
        assertEquals("Johnson", member.getLastname());
        assertNotNull(member.getDateOfBirth());
        assertEquals(2, member.getPolicies().size());
        
        String toString = member.toString();
        assertTrue(toString.contains("PARTY-CORP-123"));
        assertTrue(toString.contains("Alice"));
        assertTrue(toString.contains("Johnson"));
    }

    @Test
    void testEmptyStringValues() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        
        // Act
        member.setId("");
        member.setPartyId("");
        member.setFirstname("");
        member.setLastname("");
        
        // Assert
        assertEquals("", member.getId());
        assertEquals("", member.getPartyId());
        assertEquals("", member.getFirstname());
        assertEquals("", member.getLastname());
    }

    @Test
    void testLongStringValues() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        String longId = "A".repeat(50);
        String longName = "B".repeat(100);
        
        // Act
        member.setId(longId);
        member.setPartyId(longId);
        member.setFirstname(longName);
        member.setLastname(longName);
        
        // Assert
        assertEquals(longId, member.getId());
        assertEquals(longId, member.getPartyId());
        assertEquals(longName, member.getFirstname());
        assertEquals(longName, member.getLastname());
    }

    @Test
    void testSpecialCharactersInNames() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        String specialName = "O'Brien-Smith";
        
        // Act
        member.setFirstname(specialName);
        member.setLastname(specialName);
        
        // Assert
        assertEquals(specialName, member.getFirstname());
        assertEquals(specialName, member.getLastname());
    }

    @Test
    void testUnicodeCharactersInNames() {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        String unicodeName = "José García";
        
        // Act
        member.setFirstname(unicodeName);
        member.setLastname(unicodeName);
        
        // Assert
        assertEquals(unicodeName, member.getFirstname());
        assertEquals(unicodeName, member.getLastname());
    }

    @Test
    void testMemberWithComplexPolicyHierarchy() throws ParseException {
        // Arrange
        InsuranceMember member = new InsuranceMember();
        member.setId("MEM-COMPLEX");
        member.setFirstname("Complex");
        member.setLastname("Member");
        
        List<Policy> policies = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Policy policy = new Policy();
            policy.setPolicyID("POL-00" + i);
            policy.setHccID("HCC-00" + i);
            
            List<Plan> plans = new ArrayList<>();
            for (int j = 1; j <= 2; j++) {
                Plan plan = new Plan();
                plan.setPlanName("Policy " + i + " Plan " + j);
                plans.add(plan);
            }
            policy.setPlans(plans);
            policies.add(policy);
        }
        
        // Act
        member.setPolicies(policies);
        
        // Assert
        assertEquals(3, member.getPolicies().size());
        assertEquals(2, member.getPolicies().get(0).getPlans().size());
        assertEquals("Policy 1 Plan 1", member.getPolicies().get(0).getPlans().get(0).getPlanName());
    }

    @Test
    void testVariousNameFormats() {
        // Test different name formats
        String[][] nameFormats = {
            {"John", "Doe"},
            {"Mary-Jane", "Watson"},
            {"O'Connor", "Smith"},
            {"Jean-Luc", "Picard"},
            {"van der", "Berg"}
        };
        
        for (String[] names : nameFormats) {
            InsuranceMember member = new InsuranceMember();
            member.setFirstname(names[0]);
            member.setLastname(names[1]);
            assertEquals(names[0], member.getFirstname());
            assertEquals(names[1], member.getLastname());
        }
    }
}
