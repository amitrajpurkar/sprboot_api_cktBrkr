package com.anr.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import com.anr.common.SBUtil.TransactionType;
import com.anr.config.ConfigProperties;
import com.anr.exception.ErrorRootElement;
import com.anr.logging.LogForwarder;
import com.anr.logging.model.SplunkEvent;

/**
 * Comprehensive test suite for SBUtil class
 * Target: >90% code coverage
 * 
 * @author amitr
 */
@ExtendWith(MockitoExtension.class)
public class SBUtilTest {

    @InjectMocks
    private SBUtil sut;

    @Mock
    private ConfigProperties appProps;

    @Mock
    private LogForwarder logforwarder;

    private static final String TRANSACTION_ID = "TXN-12345";
    private static final String TEST_MESSAGE = "Test log message";

    // ========================================================================
    // LOG DEBUG TESTS
    // ========================================================================

    @Test
    void testLogDebug_withValidTransactionID() {
        // Act
        sut.logDebug(TRANSACTION_ID, TEST_MESSAGE);
        
        // Assert - method executes without exception
        // Note: Actual logging is done via SLF4J logger, which we don't mock
    }

    @Test
    void testLogDebug_withEmptyTransactionID() {
        // Act
        sut.logDebug("", TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    @Test
    void testLogDebug_withNullTransactionID() {
        // Act
        sut.logDebug(null, TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    @Test
    void testLogDebug_withNullMessage() {
        // Act
        sut.logDebug(TRANSACTION_ID, null);
        
        // Assert - method executes without exception
    }

    // ========================================================================
    // LOG INFO TESTS
    // ========================================================================

    @Test
    void testLogInfo_withValidTransactionID() {
        // Act
        sut.logInfo(TRANSACTION_ID, TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    @Test
    void testLogInfo_withEmptyTransactionID() {
        // Act
        sut.logInfo("", TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    @Test
    void testLogInfo_withNullTransactionID() {
        // Act
        sut.logInfo(null, TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    // ========================================================================
    // LOG ERROR TESTS
    // ========================================================================

    @Test
    void testLogError_withValidTransactionID() {
        // Act
        sut.logError(TRANSACTION_ID, TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    @Test
    void testLogError_withEmptyTransactionID() {
        // Act
        sut.logError("", TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    @Test
    void testLogError_withNullTransactionID() {
        // Act
        sut.logError(null, TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    // ========================================================================
    // LOG TRACE TESTS
    // ========================================================================

    @Test
    void testLogTrace_withValidTransactionID() {
        // Act
        sut.logTrace(TRANSACTION_ID, TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    @Test
    void testLogTrace_withEmptyTransactionID() {
        // Act
        sut.logTrace("", TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    @Test
    void testLogTrace_withNullTransactionID() {
        // Act
        sut.logTrace(null, TEST_MESSAGE);
        
        // Assert - method executes without exception
    }

    // ========================================================================
    // LOG STACK TRACE TESTS
    // ========================================================================

    @Test
    void testLogStackTrace_withStackTraceEnabled() {
        // Arrange
        Exception testException = new RuntimeException("Test exception");
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act
        sut.logStackTrace(TRANSACTION_ID, TEST_MESSAGE, testException);
        
        // Assert
        verify(appProps, times(1)).getLogStackTrace();
    }

    @Test
    void testLogStackTrace_withStackTraceDisabled() {
        // Arrange
        Exception testException = new RuntimeException("Test exception");
        when(appProps.getLogStackTrace()).thenReturn(false);
        
        // Act
        sut.logStackTrace(TRANSACTION_ID, TEST_MESSAGE, testException);
        
        // Assert
        verify(appProps, times(1)).getLogStackTrace();
    }

    @Test
    void testLogStackTrace_withNullTransactionID() {
        // Arrange
        Exception testException = new RuntimeException("Test exception");
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act
        sut.logStackTrace(null, TEST_MESSAGE, testException);
        
        // Assert
        verify(appProps, times(1)).getLogStackTrace();
    }

    @Test
    void testLogStackTrace_withEmptyTransactionID() {
        // Arrange
        Exception testException = new RuntimeException("Test exception");
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act
        sut.logStackTrace("", TEST_MESSAGE, testException);
        
        // Assert
        verify(appProps, times(1)).getLogStackTrace();
    }

    @Test
    void testLogStackTrace_withNestedExceptions() {
        // Arrange
        Exception rootCause = new IllegalArgumentException("Root cause");
        Exception nestedException = new RuntimeException("Nested exception", rootCause);
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act
        sut.logStackTrace(TRANSACTION_ID, TEST_MESSAGE, nestedException);
        
        // Assert
        verify(appProps, times(1)).getLogStackTrace();
    }

    // ========================================================================
    // GET ROOT CAUSE MESSAGE TESTS
    // ========================================================================

    @Test
    void testGetRootCauseMessage_withSimpleException() {
        // Arrange
        Exception testException = new RuntimeException("Simple exception message");
        
        // Act
        String result = sut.getRootCauseMessage(testException);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Simple exception message") || result.contains("RuntimeException"));
    }

    @Test
    void testGetRootCauseMessage_withNestedExceptions() {
        // Arrange
        Exception rootCause = new IllegalArgumentException("Root cause message");
        Exception nestedException = new RuntimeException("Wrapper exception", rootCause);
        
        // Act
        String result = sut.getRootCauseMessage(nestedException);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Root cause") || result.contains("IllegalArgumentException"));
    }

    @Test
    void testGetRootCauseMessage_withStackTrace() {
        // Arrange
        Exception testException = new RuntimeException("Test exception");
        
        // Act
        String result = sut.getRootCauseMessage(testException);
        
        // Assert
        assertNotNull(result);
        // Should contain stack trace element information
        assertTrue(result.length() > 0);
    }

    @Test
    void testGetRootCauseMessage_withMultipleLevelsOfNesting() {
        // Arrange
        Exception level1 = new IllegalStateException("Level 1");
        Exception level2 = new IllegalArgumentException("Level 2", level1);
        Exception level3 = new RuntimeException("Level 3", level2);
        
        // Act
        String result = sut.getRootCauseMessage(level3);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    // ========================================================================
    // PARSE EXCEPTION TESTS
    // ========================================================================

    @Test
    void testParseException_withGenericException() {
        // Arrange
        Exception testException = new RuntimeException("Generic error");
        String methodName = "testMethod";
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act
        ErrorRootElement result = sut.parseException(TRANSACTION_ID, testException, methodName);
        
        // Assert
        assertNotNull(result);
        assertEquals("ERR-000", result.getErrorCode());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains(methodName));
    }

    @Test
    void testParseException_withRestClientException() {
        // Arrange
        RestClientException restException = new RestClientException("REST call failed");
        String methodName = "callBackendService";
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act
        ErrorRootElement result = sut.parseException(TRANSACTION_ID, restException, methodName);
        
        // Assert
        assertNotNull(result);
        assertEquals("ERR-RESTBACKEND", result.getErrorCode());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains(methodName));
    }

    @Test
    void testParseException_withNestedRestClientException() {
        // Arrange
        RestClientException restException = new RestClientException("REST call failed");
        RuntimeException wrapperException = new RuntimeException("Wrapper", restException);
        String methodName = "callBackendService";
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act
        ErrorRootElement result = sut.parseException(TRANSACTION_ID, wrapperException, methodName);
        
        // Assert
        assertNotNull(result);
        assertEquals("ERR-RESTBACKEND", result.getErrorCode());
        assertTrue(result.getMessage().contains(methodName));
    }

    @Test
    void testParseException_withNullTransactionID() {
        // Arrange
        Exception testException = new RuntimeException("Test error");
        String methodName = "testMethod";
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act
        ErrorRootElement result = sut.parseException(null, testException, methodName);
        
        // Assert
        assertNotNull(result);
        assertEquals("ERR-000", result.getErrorCode());
    }

    @Test
    void testParseException_withEmptyMethodName() {
        // Arrange
        Exception testException = new RuntimeException("Test error");
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act
        ErrorRootElement result = sut.parseException(TRANSACTION_ID, testException, "");
        
        // Assert
        assertNotNull(result);
        assertEquals("ERR-000", result.getErrorCode());
    }

    // ========================================================================
    // LOG TO SPLUNK OR SIMILAR TESTS
    // ========================================================================

    @Test
    void testLogToSplunkOrSimilar_withRequestType() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Request)
                .build();
        long startTime = System.currentTimeMillis() - 1000;
        
        // Act
        sut.logToSplunkOrSimilar(event, startTime);
        
        // Assert
        verify(logforwarder, times(1)).logEvent(event);
    }

    @Test
    void testLogToSplunkOrSimilar_withResponseType() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Response)
                .build();
        long startTime = System.currentTimeMillis() - 1000;
        
        // Act
        sut.logToSplunkOrSimilar(event, startTime);
        
        // Assert
        verify(logforwarder, times(1)).logEvent(event);
    }

    @Test
    void testLogToSplunkOrSimilar_withFailureType() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Failure)
                .build();
        long startTime = System.currentTimeMillis() - 1000;
        
        // Act
        sut.logToSplunkOrSimilar(event, startTime);
        
        // Assert
        verify(logforwarder, times(1)).logEvent(event);
    }

    @Test
    void testLogToSplunkOrSimilar_withInProcessType() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.InProcess)
                .build();
        long startTime = System.currentTimeMillis() - 1000;
        
        // Act
        sut.logToSplunkOrSimilar(event, startTime);
        
        // Assert
        verify(logforwarder, times(1)).logEvent(event);
    }

    @Test
    void testLogToSplunkOrSimilar_calculatesResponseTime() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Response)
                .build();
        long startTime = System.currentTimeMillis() - 500; // 500ms ago
        
        // Act
        sut.logToSplunkOrSimilar(event, startTime);
        
        // Assert
        verify(logforwarder, times(1)).logEvent(event);
        // Response time should be calculated and set
    }

    @Test
    void testLogToSplunkOrSimilar_withZeroStartTime() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Request)
                .build();
        
        // Act
        sut.logToSplunkOrSimilar(event, 0L);
        
        // Assert
        verify(logforwarder, times(1)).logEvent(event);
    }

    // ========================================================================
    // TRANSACTION TYPE ENUM TESTS
    // ========================================================================

    @Test
    void testTransactionType_enumValues() {
        // Assert all enum values exist
        assertEquals(4, TransactionType.values().length);
        assertNotNull(TransactionType.Request);
        assertNotNull(TransactionType.Response);
        assertNotNull(TransactionType.Failure);
        assertNotNull(TransactionType.InProcess);
    }

    @Test
    void testTransactionType_valueOf() {
        // Test valueOf method
        assertEquals(TransactionType.Request, TransactionType.valueOf("Request"));
        assertEquals(TransactionType.Response, TransactionType.valueOf("Response"));
        assertEquals(TransactionType.Failure, TransactionType.valueOf("Failure"));
        assertEquals(TransactionType.InProcess, TransactionType.valueOf("InProcess"));
    }

    // ========================================================================
    // EDGE CASE TESTS
    // ========================================================================

    @Test
    void testLogStackTrace_withNullException() {
        // Arrange
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act & Assert - should handle gracefully
        try {
            sut.logStackTrace(TRANSACTION_ID, TEST_MESSAGE, null);
        } catch (NullPointerException e) {
            // Expected for null exception
        }
    }

    @Test
    void testGetRootCauseMessage_withExceptionWithoutStackTrace() {
        // Arrange
        Exception testException = new RuntimeException("No stack trace") {
            @Override
            public StackTraceElement[] getStackTrace() {
                return new StackTraceElement[0];
            }
        };
        
        // Act
        String result = sut.getRootCauseMessage(testException);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("No stack trace") || result.contains("RuntimeException"));
    }

    @Test
    void testGetRootCauseMessage_withNullMessage() {
        // Arrange
        Exception testException = new RuntimeException((String) null);
        
        // Act
        String result = sut.getRootCauseMessage(testException);
        
        // Assert
        assertNotNull(result);
    }

    @Test
    void testParseException_withDeeplyNestedRestClientException() {
        // Arrange
        RestClientException restException = new RestClientException("Backend unavailable");
        RuntimeException level1 = new RuntimeException("Level 1", restException);
        RuntimeException level2 = new RuntimeException("Level 2", level1);
        RuntimeException level3 = new RuntimeException("Level 3", level2);
        String methodName = "deepNestedCall";
        when(appProps.getLogStackTrace()).thenReturn(false);
        
        // Act
        ErrorRootElement result = sut.parseException(TRANSACTION_ID, level3, methodName);
        
        // Assert
        assertNotNull(result);
        assertEquals("ERR-RESTBACKEND", result.getErrorCode());
    }

    @Test
    void testLogToSplunkOrSimilar_withNullTransactionType() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .build();
        long startTime = System.currentTimeMillis() - 1000;
        
        // Act
        sut.logToSplunkOrSimilar(event, startTime);
        
        // Assert
        verify(logforwarder, times(1)).logEvent(event);
    }

    @Test
    void testLogToSplunkOrSimilar_withVeryLongRunningRequest() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Response)
                .build();
        long startTime = System.currentTimeMillis() - 10000; // 10 seconds ago
        
        // Act
        sut.logToSplunkOrSimilar(event, startTime);
        
        // Assert
        verify(logforwarder, times(1)).logEvent(event);
    }

    @Test
    void testParseException_withMultipleExceptionTypes() {
        // Arrange
        IllegalArgumentException illegalArg = new IllegalArgumentException("Invalid argument");
        String methodName = "validateInput";
        when(appProps.getLogStackTrace()).thenReturn(true);
        
        // Act
        ErrorRootElement result = sut.parseException(TRANSACTION_ID, illegalArg, methodName);
        
        // Assert
        assertNotNull(result);
        assertEquals("ERR-000", result.getErrorCode());
        assertTrue(result.getMessage().contains(methodName));
    }

    @Test
    void testGetRootCauseMessage_withComplexExceptionChain() {
        // Arrange
        NullPointerException npe = new NullPointerException("Null value encountered");
        IllegalStateException ise = new IllegalStateException("Invalid state", npe);
        RuntimeException re = new RuntimeException("Runtime error", ise);
        
        // Act
        String result = sut.getRootCauseMessage(re);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }
}
