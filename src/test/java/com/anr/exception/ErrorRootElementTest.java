package com.anr.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Comprehensive test suite for ErrorRootElement class
 * Target: >90% code coverage
 * 
 * @author amitr
 */
public class ErrorRootElementTest {

    private static final String ERROR_CODE = "ERR-001";
    private static final String ERROR_MESSAGE = "Test error message";
    private static final String TECH_MESSAGE = "Technical error details";

    // ========================================================================
    // CONSTRUCTOR TESTS
    // ========================================================================

    @Test
    void testDefaultConstructor() {
        // Act
        ErrorRootElement error = new ErrorRootElement();
        
        // Assert
        assertNotNull(error);
        assertNull(error.getErrorCode());
        assertNull(error.getMessage());
        assertNull(error.getTechMessage());
    }

    @Test
    void testConstructorWithCodeAndMessage() {
        // Act
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE);
        
        // Assert
        assertNotNull(error);
        assertEquals(ERROR_CODE, error.getErrorCode());
        assertEquals(ERROR_MESSAGE, error.getMessage());
        assertNull(error.getTechMessage());
    }

    @Test
    void testConstructorWithCodeMessageAndTechMessage() {
        // Act
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE, TECH_MESSAGE);
        
        // Assert
        assertNotNull(error);
        assertEquals(ERROR_CODE, error.getErrorCode());
        assertEquals(ERROR_MESSAGE, error.getMessage());
        assertEquals(TECH_MESSAGE, error.getTechMessage());
    }

    @Test
    void testConstructorWithNullCode() {
        // Act
        ErrorRootElement error = new ErrorRootElement(null, ERROR_MESSAGE);
        
        // Assert
        assertNotNull(error);
        assertNull(error.getErrorCode());
        assertEquals(ERROR_MESSAGE, error.getMessage());
    }

    @Test
    void testConstructorWithNullMessage() {
        // Act
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, null);
        
        // Assert
        assertNotNull(error);
        assertEquals(ERROR_CODE, error.getErrorCode());
        assertNull(error.getMessage());
    }

    @Test
    void testConstructorWithNullTechMessage() {
        // Act
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE, null);
        
        // Assert
        assertNotNull(error);
        assertEquals(ERROR_CODE, error.getErrorCode());
        assertEquals(ERROR_MESSAGE, error.getMessage());
        assertNull(error.getTechMessage());
    }

    @Test
    void testConstructorWithAllNulls() {
        // Act
        ErrorRootElement error = new ErrorRootElement(null, null, null);
        
        // Assert
        assertNotNull(error);
        assertNull(error.getErrorCode());
        assertNull(error.getMessage());
        assertNull(error.getTechMessage());
    }

    @Test
    void testConstructorWithEmptyStrings() {
        // Act
        ErrorRootElement error = new ErrorRootElement("", "", "");
        
        // Assert
        assertNotNull(error);
        assertEquals("", error.getErrorCode());
        assertEquals("", error.getMessage());
        assertEquals("", error.getTechMessage());
    }

    // ========================================================================
    // GETTER AND SETTER TESTS
    // ========================================================================

    @Test
    void testSetAndGetErrorCode() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement();
        
        // Act
        error.setErrorCode(ERROR_CODE);
        
        // Assert
        assertEquals(ERROR_CODE, error.getErrorCode());
    }

    @Test
    void testSetAndGetMessage() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement();
        
        // Act
        error.setMessage(ERROR_MESSAGE);
        
        // Assert
        assertEquals(ERROR_MESSAGE, error.getMessage());
    }

    @Test
    void testSetAndGetTechMessage() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement();
        
        // Act
        error.setTechMessage(TECH_MESSAGE);
        
        // Assert
        assertEquals(TECH_MESSAGE, error.getTechMessage());
    }

    @Test
    void testSetNullErrorCode() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE);
        
        // Act
        error.setErrorCode(null);
        
        // Assert
        assertNull(error.getErrorCode());
    }

    @Test
    void testSetNullMessage() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE);
        
        // Act
        error.setMessage(null);
        
        // Assert
        assertNull(error.getMessage());
    }

    @Test
    void testSetNullTechMessage() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE, TECH_MESSAGE);
        
        // Act
        error.setTechMessage(null);
        
        // Assert
        assertNull(error.getTechMessage());
    }

    @Test
    void testOverwriteErrorCode() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE);
        String newCode = "ERR-002";
        
        // Act
        error.setErrorCode(newCode);
        
        // Assert
        assertEquals(newCode, error.getErrorCode());
    }

    @Test
    void testOverwriteMessage() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE);
        String newMessage = "Updated error message";
        
        // Act
        error.setMessage(newMessage);
        
        // Assert
        assertEquals(newMessage, error.getMessage());
    }

    @Test
    void testOverwriteTechMessage() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE, TECH_MESSAGE);
        String newTechMessage = "Updated technical details";
        
        // Act
        error.setTechMessage(newTechMessage);
        
        // Assert
        assertEquals(newTechMessage, error.getTechMessage());
    }

    // ========================================================================
    // TO STRING TESTS
    // ========================================================================

    @Test
    void testToString_withAllFields() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE, TECH_MESSAGE);
        
        // Act
        String result = error.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains(ERROR_CODE));
        assertTrue(result.contains(ERROR_MESSAGE));
        assertTrue(result.contains("ErrorRootElement"));
    }

    @Test
    void testToString_withCodeAndMessage() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE);
        
        // Act
        String result = error.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains(ERROR_CODE));
        assertTrue(result.contains(ERROR_MESSAGE));
    }

    @Test
    void testToString_withNullFields() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement();
        
        // Act
        String result = error.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("ErrorRootElement"));
    }

    @Test
    void testToString_format() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement("ERR-123", "Test message");
        
        // Act
        String result = error.toString();
        
        // Assert
        assertEquals("ErrorRootElement [errorCode=ERR-123, message=Test message]", result);
    }

    // ========================================================================
    // JSON SERIALIZATION TESTS
    // ========================================================================

    @Test
    void testJsonSerialization_withAllFields() throws JsonProcessingException {
        // Arrange
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE, TECH_MESSAGE);
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        String json = mapper.writeValueAsString(error);
        
        // Assert
        assertNotNull(json);
        assertTrue(json.contains(ERROR_CODE));
        assertTrue(json.contains(ERROR_MESSAGE));
        assertTrue(json.contains(TECH_MESSAGE));
    }

    @Test
    void testJsonSerialization_withNullFields() throws JsonProcessingException {
        // Arrange
        ErrorRootElement error = new ErrorRootElement();
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        String json = mapper.writeValueAsString(error);
        
        // Assert
        assertNotNull(json);
        // JsonInclude(Include.NON_NULL) should exclude null fields
        assertEquals("{}", json);
    }

    @Test
    void testJsonSerialization_withPartialFields() throws JsonProcessingException {
        // Arrange
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, ERROR_MESSAGE);
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        String json = mapper.writeValueAsString(error);
        
        // Assert
        assertNotNull(json);
        assertTrue(json.contains(ERROR_CODE));
        assertTrue(json.contains(ERROR_MESSAGE));
        // techMessage should not be in JSON as it's null
    }

    @Test
    void testJsonDeserialization() throws JsonProcessingException {
        // Arrange
        String json = "{\"errorCode\":\"ERR-001\",\"message\":\"Test error\",\"techMessage\":\"Tech details\"}";
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        ErrorRootElement error = mapper.readValue(json, ErrorRootElement.class);
        
        // Assert
        assertNotNull(error);
        assertEquals("ERR-001", error.getErrorCode());
        assertEquals("Test error", error.getMessage());
        assertEquals("Tech details", error.getTechMessage());
    }

    @Test
    void testJsonDeserialization_withMissingFields() throws JsonProcessingException {
        // Arrange
        String json = "{\"errorCode\":\"ERR-001\"}";
        ObjectMapper mapper = new ObjectMapper();
        
        // Act
        ErrorRootElement error = mapper.readValue(json, ErrorRootElement.class);
        
        // Assert
        assertNotNull(error);
        assertEquals("ERR-001", error.getErrorCode());
        assertNull(error.getMessage());
        assertNull(error.getTechMessage());
    }

    // ========================================================================
    // INTEGRATION TESTS
    // ========================================================================

    @Test
    void testCompleteWorkflow() {
        // Arrange
        ErrorRootElement error = new ErrorRootElement();
        
        // Act
        error.setErrorCode("ERR-500");
        error.setMessage("Internal server error");
        error.setTechMessage("NullPointerException at line 42");
        
        // Assert
        assertEquals("ERR-500", error.getErrorCode());
        assertEquals("Internal server error", error.getMessage());
        assertEquals("NullPointerException at line 42", error.getTechMessage());
        assertTrue(error.toString().contains("ERR-500"));
    }

    @Test
    void testErrorCodePatterns() {
        // Test various error code patterns
        String[] errorCodes = {
            "ERR-000",
            "ERR-RESTBACKEND",
            "ERR-VALIDATION",
            "ERR-AUTH-001",
            "SYS-ERROR-500"
        };
        
        for (String code : errorCodes) {
            ErrorRootElement error = new ErrorRootElement(code, "Test message");
            assertEquals(code, error.getErrorCode());
        }
    }

    @Test
    void testLongMessages() {
        // Arrange
        String longMessage = "This is a very long error message that contains detailed information " +
                "about what went wrong, including stack traces, request parameters, and various " +
                "debugging information that might be useful for troubleshooting the issue.";
        String longTechMessage = "Technical details: " + longMessage;
        
        // Act
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, longMessage, longTechMessage);
        
        // Assert
        assertEquals(longMessage, error.getMessage());
        assertEquals(longTechMessage, error.getTechMessage());
    }

    @Test
    void testSpecialCharactersInMessages() {
        // Arrange
        String messageWithSpecialChars = "Error: Invalid input @#$%^&*() <> {} [] | \\ / ? ~ `";
        
        // Act
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, messageWithSpecialChars);
        
        // Assert
        assertEquals(messageWithSpecialChars, error.getMessage());
    }

    @Test
    void testMultilineMessages() {
        // Arrange
        String multilineMessage = "Error occurred:\nLine 1: Validation failed\nLine 2: Field 'name' is required";
        
        // Act
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, multilineMessage);
        
        // Assert
        assertEquals(multilineMessage, error.getMessage());
    }

    @Test
    void testUnicodeCharacters() {
        // Arrange
        String unicodeMessage = "Error: 用户名无效 (Invalid username) - Ошибка";
        
        // Act
        ErrorRootElement error = new ErrorRootElement(ERROR_CODE, unicodeMessage);
        
        // Assert
        assertEquals(unicodeMessage, error.getMessage());
    }
}
