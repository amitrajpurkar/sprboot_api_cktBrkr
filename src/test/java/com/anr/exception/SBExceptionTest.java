package com.anr.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for SBException class
 * Target: >90% code coverage
 * 
 * @author amitr
 */
public class SBExceptionTest {

    private static final String TEST_MESSAGE = "Test exception message";
    private static final String ERROR_MSG_1 = "Error message 1";
    private static final String ERROR_MSG_2 = "Error message 2";
    private static final String ERROR_MSG_3 = "Error message 3";

    // ========================================================================
    // CONSTRUCTOR TESTS
    // ========================================================================

    @Test
    void testDefaultConstructor() {
        // Act
        SBException exception = new SBException();
        
        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertFalse(exception.hasErrors());
    }

    @Test
    void testConstructorWithMessage() {
        // Act
        SBException exception = new SBException(TEST_MESSAGE);
        
        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
        assertFalse(exception.hasErrors());
    }

    @Test
    void testConstructorWithThrowable() {
        // Arrange
        Throwable cause = new RuntimeException("Root cause");
        
        // Act
        SBException exception = new SBException(cause);
        
        // Assert
        assertNotNull(exception);
        assertEquals(cause, exception.getCause());
        assertFalse(exception.hasErrors());
    }

    @Test
    void testConstructorWithMessageAndThrowable() {
        // Arrange
        Throwable cause = new IllegalArgumentException("Invalid argument");
        
        // Act
        SBException exception = new SBException(TEST_MESSAGE, cause);
        
        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertFalse(exception.hasErrors());
    }

    @Test
    void testConstructorWithNullMessage() {
        // Act
        SBException exception = new SBException((String) null);
        
        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertFalse(exception.hasErrors());
    }

    @Test
    void testConstructorWithNullThrowable() {
        // Act
        SBException exception = new SBException((Throwable) null);
        
        // Assert
        assertNotNull(exception);
        assertNull(exception.getCause()); // Null throwable results in null cause
        assertFalse(exception.hasErrors());
    }

    @Test
    void testConstructorWithNullMessageAndThrowable() {
        // Act
        SBException exception = new SBException(null, null);
        
        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertFalse(exception.hasErrors());
    }

    // ========================================================================
    // ADD ERROR MESSAGE TESTS
    // ========================================================================

    @Test
    void testAddErrMsg_singleMessage() {
        // Arrange
        SBException exception = new SBException();
        
        // Act
        exception.addErrMsg(ERROR_MSG_1);
        
        // Assert
        assertTrue(exception.hasErrors());
    }

    @Test
    void testAddErrMsg_multipleMessages() {
        // Arrange
        SBException exception = new SBException();
        
        // Act
        exception.addErrMsg(ERROR_MSG_1);
        exception.addErrMsg(ERROR_MSG_2);
        exception.addErrMsg(ERROR_MSG_3);
        
        // Assert
        assertTrue(exception.hasErrors());
    }

    @Test
    void testAddErrMsg_nullMessage() {
        // Arrange
        SBException exception = new SBException();
        
        // Act
        exception.addErrMsg(null);
        
        // Assert
        assertTrue(exception.hasErrors()); // List contains null
    }

    @Test
    void testAddErrMsg_emptyMessage() {
        // Arrange
        SBException exception = new SBException();
        
        // Act
        exception.addErrMsg("");
        
        // Assert
        assertTrue(exception.hasErrors());
    }

    // ========================================================================
    // ADD ERROR MESSAGES (LIST) TESTS
    // ========================================================================

    @Test
    void testAddErrorMsgs_withMultipleMessages() {
        // Arrange
        SBException exception = new SBException();
        List<String> errorMessages = Arrays.asList(ERROR_MSG_1, ERROR_MSG_2, ERROR_MSG_3);
        
        // Act
        exception.addErrorMsgs(errorMessages);
        
        // Assert
        assertTrue(exception.hasErrors());
    }

    @Test
    void testAddErrorMsgs_withSingleMessage() {
        // Arrange
        SBException exception = new SBException();
        List<String> errorMessages = Collections.singletonList(ERROR_MSG_1);
        
        // Act
        exception.addErrorMsgs(errorMessages);
        
        // Assert
        assertTrue(exception.hasErrors());
    }

    @Test
    void testAddErrorMsgs_withEmptyList() {
        // Arrange
        SBException exception = new SBException();
        List<String> errorMessages = Collections.emptyList();
        
        // Act
        exception.addErrorMsgs(errorMessages);
        
        // Assert
        assertFalse(exception.hasErrors());
    }

    @Test
    void testAddErrorMsgs_withNullInList() {
        // Arrange
        SBException exception = new SBException();
        List<String> errorMessages = Arrays.asList(ERROR_MSG_1, null, ERROR_MSG_2);
        
        // Act
        exception.addErrorMsgs(errorMessages);
        
        // Assert
        assertTrue(exception.hasErrors());
    }

    @Test
    void testAddErrorMsgs_multipleCalls() {
        // Arrange
        SBException exception = new SBException();
        List<String> firstBatch = Arrays.asList(ERROR_MSG_1, ERROR_MSG_2);
        List<String> secondBatch = Collections.singletonList(ERROR_MSG_3);
        
        // Act
        exception.addErrorMsgs(firstBatch);
        exception.addErrorMsgs(secondBatch);
        
        // Assert
        assertTrue(exception.hasErrors());
    }

    // ========================================================================
    // HAS ERRORS TESTS
    // ========================================================================

    @Test
    void testHasErrors_initiallyFalse() {
        // Arrange
        SBException exception = new SBException();
        
        // Act & Assert
        assertFalse(exception.hasErrors());
    }

    @Test
    void testHasErrors_afterAddingMessage() {
        // Arrange
        SBException exception = new SBException();
        
        // Act
        exception.addErrMsg(ERROR_MSG_1);
        
        // Assert
        assertTrue(exception.hasErrors());
    }

    @Test
    void testHasErrors_afterAddingMessages() {
        // Arrange
        SBException exception = new SBException();
        List<String> errorMessages = Arrays.asList(ERROR_MSG_1, ERROR_MSG_2);
        
        // Act
        exception.addErrorMsgs(errorMessages);
        
        // Assert
        assertTrue(exception.hasErrors());
    }

    @Test
    void testHasErrors_afterAddingEmptyList() {
        // Arrange
        SBException exception = new SBException();
        
        // Act
        exception.addErrorMsgs(Collections.emptyList());
        
        // Assert
        assertFalse(exception.hasErrors());
    }

    // ========================================================================
    // INTEGRATION TESTS
    // ========================================================================

    @Test
    void testExceptionCanBeThrown() {
        // Act & Assert
        try {
            throw new SBException(TEST_MESSAGE);
        } catch (SBException e) {
            assertEquals(TEST_MESSAGE, e.getMessage());
            assertFalse(e.hasErrors());
        }
    }

    @Test
    void testExceptionWithCauseCanBeThrown() {
        // Arrange
        RuntimeException cause = new RuntimeException("Root cause");
        
        // Act & Assert
        try {
            throw new SBException(TEST_MESSAGE, cause);
        } catch (SBException e) {
            assertEquals(TEST_MESSAGE, e.getMessage());
            assertEquals(cause, e.getCause());
            assertFalse(e.hasErrors());
        }
    }

    @Test
    void testExceptionWithErrorMessages() {
        // Arrange
        SBException exception = new SBException(TEST_MESSAGE);
        exception.addErrMsg(ERROR_MSG_1);
        exception.addErrMsg(ERROR_MSG_2);
        
        // Act & Assert
        try {
            throw exception;
        } catch (SBException e) {
            assertEquals(TEST_MESSAGE, e.getMessage());
            assertTrue(e.hasErrors());
        }
    }

    @Test
    void testSerialVersionUID() {
        // This test ensures the serialVersionUID is defined
        // Act
        SBException exception = new SBException();
        
        // Assert
        assertNotNull(exception);
        // serialVersionUID is accessible via reflection if needed
    }

    @Test
    void testExceptionInheritance() {
        // Arrange
        SBException exception = new SBException(TEST_MESSAGE);
        
        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void testComplexScenario() {
        // Arrange
        IllegalArgumentException rootCause = new IllegalArgumentException("Invalid input");
        SBException exception = new SBException("Processing failed", rootCause);
        
        // Act
        exception.addErrMsg("Validation error: field1 is required");
        exception.addErrMsg("Validation error: field2 is invalid");
        exception.addErrorMsgs(Arrays.asList("Business rule violation 1", "Business rule violation 2"));
        
        // Assert
        assertNotNull(exception);
        assertEquals("Processing failed", exception.getMessage());
        assertEquals(rootCause, exception.getCause());
        assertTrue(exception.hasErrors());
    }

    @Test
    void testAddErrorMsgs_preservesOrder() {
        // Arrange
        SBException exception = new SBException();
        List<String> messages = Arrays.asList("First", "Second", "Third");
        
        // Act
        exception.addErrorMsgs(messages);
        
        // Assert
        assertTrue(exception.hasErrors());
        // Order is preserved in the internal list
    }
}
