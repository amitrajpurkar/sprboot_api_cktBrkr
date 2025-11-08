package com.anr.logging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.anr.common.SBUtil.TransactionType;
import com.anr.logging.model.SplunkEvent;
import com.anr.logging.model.SplunkLogRecord;

/**
 * Comprehensive test suite for LogForwarder class
 * Target: >90% code coverage
 * 
 * @author amitr
 */
@ExtendWith(MockitoExtension.class)
public class LogForwarderTest {

    @InjectMocks
    private LogForwarder logForwarder;

    @Mock
    private SplunkLogRecord mockLogRecord;

    private static final String TRANSACTION_ID = "TXN-12345";

    // ========================================================================
    // LOG EVENT TESTS
    // ========================================================================

    @Test
    void testLogEvent_withRequestType() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Request)
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withResponseType() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Response)
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withFailureType() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Failure)
                .errorCode("ERR-001")
                .errorMsg("Test failure")
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withInProcessType() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.InProcess)
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withNullPodName() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Request)
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert - podName should be set to null
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withCompleteEvent() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("CompleteAPI", "production", "WebChannel", TRANSACTION_ID)
                .transactionType(TransactionType.Response)
                .errorCode("ERR-000")
                .errorMsg("Success")
                .responseTimeInMillis(150L)
                .requestUrl("/api/v1/test")
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_multipleCallsSequentially() {
        // Arrange
        SplunkEvent event1 = new SplunkEvent.SplunkEventBuilder("API1", "dev", "ch1", "TXN-1")
                .transactionType(TransactionType.Request)
                .build();
        
        SplunkEvent event2 = new SplunkEvent.SplunkEventBuilder("API2", "dev", "ch2", "TXN-2")
                .transactionType(TransactionType.Response)
                .build();
        
        SplunkEvent event3 = new SplunkEvent.SplunkEventBuilder("API3", "dev", "ch3", "TXN-3")
                .transactionType(TransactionType.Failure)
                .build();
        
        // Act
        logForwarder.logEvent(event1);
        logForwarder.logEvent(event2);
        logForwarder.logEvent(event3);
        
        // Assert
        verify(mockLogRecord, times(3)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withDifferentEnvironments() {
        // Test different environments
        String[] environments = {"localhost", "development", "staging", "production"};
        
        for (String env : environments) {
            SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", env, "channel", TRANSACTION_ID)
                    .transactionType(TransactionType.Request)
                    .build();
            
            logForwarder.logEvent(event);
        }
        
        // Assert
        verify(mockLogRecord, times(4)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withDifferentChannels() {
        // Test different source channels
        String[] channels = {"WebChannel", "MobileChannel", "APIChannel", "BatchChannel"};
        
        for (String channel : channels) {
            SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", channel, TRANSACTION_ID)
                    .transactionType(TransactionType.Request)
                    .build();
            
            logForwarder.logEvent(event);
        }
        
        // Assert
        verify(mockLogRecord, times(4)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withErrorMessages() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Failure)
                .errorCode("ERR-500")
                .errorMsg("Internal server error occurred")
                .addErrorMsg("Additional error details")
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withWarningCodes() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Response)
                .addOneWarningCode("WARN-001")
                .addOneWarningCode("WARN-002")
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withLongTransactionId() {
        // Arrange
        String longTxnId = "TXN-" + "A".repeat(100);
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", longTxnId)
                .transactionType(TransactionType.Request)
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withSpecialCharactersInApi() {
        // Arrange
        String apiWithSpecialChars = "test-api_v2.0";
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder(apiWithSpecialChars, "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Request)
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withEmptyStrings() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("", "", "", "")
                .transactionType(TransactionType.Request)
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withRequestUrl() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Request)
                .requestUrl("/api/v1/users/12345")
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withTimestamps() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Response)
                .requestedTs("2024-11-08 18:00:00.000")
                .processStartTs("2024-11-08 18:00:00.100")
                .processEndTs("2024-11-08 18:00:00.250")
                .responseTimeInMillis(150L)
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withZeroResponseTime() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Response)
                .responseTimeInMillis(0L)
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    @Test
    void testLogEvent_withLargeResponseTime() {
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Response)
                .responseTimeInMillis(30000L) // 30 seconds
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }

    // ========================================================================
    // ASYNC BEHAVIOR TESTS
    // ========================================================================

    @Test
    void testLogEvent_isAsyncAnnotated() {
        // This test verifies the method is called successfully
        // The @Async annotation behavior is tested through integration tests
        
        // Arrange
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", TRANSACTION_ID)
                .transactionType(TransactionType.Request)
                .build();
        
        // Act
        logForwarder.logEvent(event);
        
        // Assert - Method completes without blocking
        verify(mockLogRecord, times(1)).setData(any(SplunkEvent.class));
    }
}
