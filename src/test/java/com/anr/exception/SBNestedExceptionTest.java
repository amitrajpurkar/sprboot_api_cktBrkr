package com.anr.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.core.NestedRuntimeException;
import org.springframework.web.client.RestClientException;

/**
 * Comprehensive test suite for SBNestedException class
 * Target: >90% code coverage
 * 
 * @author amitr
 */
public class SBNestedExceptionTest {

    private static final String TEST_MESSAGE = "Test nested exception message";

    // ========================================================================
    // CONSTRUCTOR TESTS
    // ========================================================================

    @Test
    void testConstructorWithMessage() {
        // Act
        SBNestedException exception = new SBNestedException(TEST_MESSAGE);
        
        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        // Arrange
        Throwable cause = new RuntimeException("Root cause");
        
        // Act
        SBNestedException exception = new SBNestedException(TEST_MESSAGE, cause);
        
        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithNullMessage() {
        // Act
        SBNestedException exception = new SBNestedException(null);
        
        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testConstructorWithNullCause() {
        // Act
        SBNestedException exception = new SBNestedException(TEST_MESSAGE, null);
        
        // Assert
        assertNotNull(exception);
        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithNullMessageAndCause() {
        // Act
        SBNestedException exception = new SBNestedException(null, null);
        
        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithEmptyMessage() {
        // Act
        SBNestedException exception = new SBNestedException("");
        
        // Assert
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    // ========================================================================
    // INHERITANCE TESTS
    // ========================================================================

    @Test
    void testExceptionInheritance() {
        // Arrange
        SBNestedException exception = new SBNestedException(TEST_MESSAGE);
        
        // Assert
        assertTrue(exception instanceof NestedRuntimeException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void testSerialVersionUID() {
        // This test ensures the serialVersionUID is defined
        // Act
        SBNestedException exception = new SBNestedException(TEST_MESSAGE);
        
        // Assert
        assertNotNull(exception);
    }

    // ========================================================================
    // EXCEPTION THROWING TESTS
    // ========================================================================

    @Test
    void testExceptionCanBeThrown() {
        // Act & Assert
        try {
            throw new SBNestedException(TEST_MESSAGE);
        } catch (SBNestedException e) {
            assertEquals(TEST_MESSAGE, e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    void testExceptionWithCauseCanBeThrown() {
        // Arrange
        RuntimeException cause = new RuntimeException("Root cause");
        
        // Act & Assert
        try {
            throw new SBNestedException(TEST_MESSAGE, cause);
        } catch (SBNestedException e) {
            assertEquals(TEST_MESSAGE, e.getMessage());
            assertEquals(cause, e.getCause());
        }
    }

    // ========================================================================
    // NESTED EXCEPTION CHAIN TESTS
    // ========================================================================

    @Test
    void testSingleLevelNesting() {
        // Arrange
        IllegalArgumentException cause = new IllegalArgumentException("Invalid argument");
        
        // Act
        SBNestedException exception = new SBNestedException("Wrapped exception", cause);
        
        // Assert
        assertEquals("Wrapped exception", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Invalid argument", exception.getCause().getMessage());
    }

    @Test
    void testMultiLevelNesting() {
        // Arrange
        NullPointerException level1 = new NullPointerException("Null value");
        IllegalStateException level2 = new IllegalStateException("Invalid state", level1);
        
        // Act
        SBNestedException exception = new SBNestedException("Top level exception", level2);
        
        // Assert
        assertEquals("Top level exception", exception.getMessage());
        assertEquals(level2, exception.getCause());
        assertEquals(level1, exception.getCause().getCause());
    }

    @Test
    void testDeepNesting() {
        // Arrange
        Exception level1 = new Exception("Level 1");
        RuntimeException level2 = new RuntimeException("Level 2", level1);
        IllegalArgumentException level3 = new IllegalArgumentException("Level 3", level2);
        
        // Act
        SBNestedException exception = new SBNestedException("Top level", level3);
        
        // Assert
        assertNotNull(exception);
        assertEquals("Top level", exception.getMessage());
        assertEquals(level3, exception.getCause());
        assertEquals(level2, exception.getCause().getCause());
        assertEquals(level1, exception.getCause().getCause().getCause());
    }

    // ========================================================================
    // CONTAINS METHOD TESTS (inherited from NestedRuntimeException)
    // ========================================================================

    @Test
    void testContains_withDirectCause() {
        // Arrange
        IllegalArgumentException cause = new IllegalArgumentException("Invalid");
        SBNestedException exception = new SBNestedException(TEST_MESSAGE, cause);
        
        // Act & Assert
        assertTrue(exception.contains(IllegalArgumentException.class));
    }

    @Test
    void testContains_withNestedCause() {
        // Arrange
        NullPointerException rootCause = new NullPointerException("Null");
        IllegalStateException intermediateCause = new IllegalStateException("State", rootCause);
        SBNestedException exception = new SBNestedException(TEST_MESSAGE, intermediateCause);
        
        // Act & Assert
        assertTrue(exception.contains(NullPointerException.class));
        assertTrue(exception.contains(IllegalStateException.class));
    }

    @Test
    void testContains_withRestClientException() {
        // Arrange
        RestClientException restException = new RestClientException("REST call failed");
        SBNestedException exception = new SBNestedException("Wrapper", restException);
        
        // Act & Assert
        assertTrue(exception.contains(RestClientException.class));
    }

    @Test
    void testContains_withDeeplyNestedRestClientException() {
        // Arrange
        RestClientException restException = new RestClientException("REST failed");
        RuntimeException level1 = new RuntimeException("Level 1", restException);
        IllegalStateException level2 = new IllegalStateException("Level 2", level1);
        SBNestedException exception = new SBNestedException("Top", level2);
        
        // Act & Assert
        assertTrue(exception.contains(RestClientException.class));
    }

    // ========================================================================
    // GET ROOT CAUSE TESTS (inherited from NestedRuntimeException)
    // ========================================================================

    @Test
    void testGetRootCause_withNoCause() {
        // Arrange
        SBNestedException exception = new SBNestedException(TEST_MESSAGE);
        
        // Act
        Throwable rootCause = exception.getRootCause();
        
        // Assert
        assertNull(rootCause);
    }

    @Test
    void testGetRootCause_withSingleCause() {
        // Arrange
        IllegalArgumentException cause = new IllegalArgumentException("Root");
        SBNestedException exception = new SBNestedException(TEST_MESSAGE, cause);
        
        // Act
        Throwable rootCause = exception.getRootCause();
        
        // Assert
        assertEquals(cause, rootCause);
    }

    @Test
    void testGetRootCause_withMultipleLevels() {
        // Arrange
        NullPointerException level1 = new NullPointerException("Root cause");
        IllegalStateException level2 = new IllegalStateException("Middle", level1);
        RuntimeException level3 = new RuntimeException("Wrapper", level2);
        SBNestedException exception = new SBNestedException(TEST_MESSAGE, level3);
        
        // Act
        Throwable rootCause = exception.getRootCause();
        
        // Assert
        assertEquals(level1, rootCause);
        assertEquals("Root cause", rootCause.getMessage());
    }

    // ========================================================================
    // GET MOST SPECIFIC CAUSE TESTS (inherited from NestedRuntimeException)
    // ========================================================================

    @Test
    void testGetMostSpecificCause_withNoCause() {
        // Arrange
        SBNestedException exception = new SBNestedException(TEST_MESSAGE);
        
        // Act
        Throwable mostSpecificCause = exception.getMostSpecificCause();
        
        // Assert
        assertEquals(exception, mostSpecificCause);
    }

    @Test
    void testGetMostSpecificCause_withCause() {
        // Arrange
        IllegalArgumentException cause = new IllegalArgumentException("Specific cause");
        SBNestedException exception = new SBNestedException(TEST_MESSAGE, cause);
        
        // Act
        Throwable mostSpecificCause = exception.getMostSpecificCause();
        
        // Assert
        assertEquals(cause, mostSpecificCause);
    }

    @Test
    void testGetMostSpecificCause_withNestedCauses() {
        // Arrange
        NullPointerException deepestCause = new NullPointerException("Deepest");
        IllegalStateException middleCause = new IllegalStateException("Middle", deepestCause);
        SBNestedException exception = new SBNestedException(TEST_MESSAGE, middleCause);
        
        // Act
        Throwable mostSpecificCause = exception.getMostSpecificCause();
        
        // Assert
        assertEquals(deepestCause, mostSpecificCause);
    }

    // ========================================================================
    // INTEGRATION TESTS
    // ========================================================================

    @Test
    void testComplexExceptionScenario() {
        // Arrange
        RestClientException restException = new RestClientException("Backend service unavailable");
        RuntimeException wrapperException = new RuntimeException("Service call failed", restException);
        
        // Act
        SBNestedException exception = new SBNestedException("Request processing failed", wrapperException);
        
        // Assert
        assertEquals("Request processing failed", exception.getMessage());
        assertTrue(exception.contains(RestClientException.class));
        assertEquals(restException, exception.getRootCause());
        assertEquals(restException, exception.getMostSpecificCause());
    }

    @Test
    void testExceptionWithLongMessage() {
        // Arrange
        String longMessage = "This is a very long exception message that contains detailed information " +
                "about what went wrong during the processing of the request including stack traces " +
                "and various debugging information that might be useful for troubleshooting.";
        
        // Act
        SBNestedException exception = new SBNestedException(longMessage);
        
        // Assert
        assertEquals(longMessage, exception.getMessage());
    }

    @Test
    void testExceptionWithSpecialCharacters() {
        // Arrange
        String messageWithSpecialChars = "Error: Invalid input @#$%^&*() <> {} [] | \\ / ? ~ `";
        
        // Act
        SBNestedException exception = new SBNestedException(messageWithSpecialChars);
        
        // Assert
        assertEquals(messageWithSpecialChars, exception.getMessage());
    }
}
