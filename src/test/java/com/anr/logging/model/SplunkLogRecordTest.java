package com.anr.logging.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.anr.common.SBUtil.TransactionType;

/**
 * Comprehensive test suite for SplunkLogRecord class
 * Target: >90% code coverage
 * 
 * @author amitr
 */
public class SplunkLogRecordTest {

    // ========================================================================
    // CONSTRUCTOR AND BASIC TESTS
    // ========================================================================

    @Test
    void testSplunkLogRecordCreation() {
        // Act
        SplunkLogRecord record = new SplunkLogRecord();
        
        // Assert
        assertNotNull(record);
        assertNull(record.getReportType());
        assertNull(record.getHostname());
        assertNull(record.getComponent());
        assertNull(record.getData());
        assertNull(record.getForward_msg_to_splunk());
        assertNull(record.getReporter());
        assertNull(record.getEnv());
        assertNull(record.getTimestamp());
        assertNull(record.getHost());
        assertNull(record.getSource());
        assertNull(record.getSourceType());
    }

    // ========================================================================
    // GETTER AND SETTER TESTS
    // ========================================================================

    @Test
    void testSetAndGetReportType() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String reportType = "ERROR";
        
        // Act
        record.setReportType(reportType);
        
        // Assert
        assertEquals(reportType, record.getReportType());
    }

    @Test
    void testSetAndGetHostname() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String hostname = "app-server-01";
        
        // Act
        record.setHostname(hostname);
        
        // Assert
        assertEquals(hostname, record.getHostname());
    }

    @Test
    void testSetAndGetComponent() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String component = "MainController";
        
        // Act
        record.setComponent(component);
        
        // Assert
        assertEquals(component, record.getComponent());
    }

    @Test
    void testSetAndGetData() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("testApi", "dev", "channel", "TXN-001")
                .transactionType(TransactionType.Request)
                .build();
        
        // Act
        record.setData(event);
        
        // Assert
        assertEquals(event, record.getData());
    }

    @Test
    void testSetAndGetForwardMsgToSplunk() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String forwardMsg = "true";
        
        // Act
        record.setForward_msg_to_splunk(forwardMsg);
        
        // Assert
        assertEquals(forwardMsg, record.getForward_msg_to_splunk());
    }

    @Test
    void testSetAndGetReporter() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String reporter = "SpringBootApp";
        
        // Act
        record.setReporter(reporter);
        
        // Assert
        assertEquals(reporter, record.getReporter());
    }

    @Test
    void testSetAndGetEnv() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String env = "production";
        
        // Act
        record.setEnv(env);
        
        // Assert
        assertEquals(env, record.getEnv());
    }

    @Test
    void testSetAndGetTimestamp() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String timestamp = "2024-11-08 18:00:00.000";
        
        // Act
        record.setTimestamp(timestamp);
        
        // Assert
        assertEquals(timestamp, record.getTimestamp());
    }

    @Test
    void testSetAndGetHost() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String host = "localhost";
        
        // Act
        record.setHost(host);
        
        // Assert
        assertEquals(host, record.getHost());
    }

    @Test
    void testSetAndGetSource() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String source = "application-logs";
        
        // Act
        record.setSource(source);
        
        // Assert
        assertEquals(source, record.getSource());
    }

    @Test
    void testSetAndGetSourceType() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String sourceType = "json";
        
        // Act
        record.setSourceType(sourceType);
        
        // Assert
        assertEquals(sourceType, record.getSourceType());
    }

    // ========================================================================
    // NULL VALUE TESTS
    // ========================================================================

    @Test
    void testSetNullValues() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        record.setReportType("ERROR");
        record.setHostname("host");
        record.setComponent("component");
        record.setForward_msg_to_splunk("true");
        record.setReporter("reporter");
        record.setEnv("dev");
        record.setTimestamp("timestamp");
        record.setHost("host");
        record.setSource("source");
        record.setSourceType("type");
        
        // Act - Set all to null
        record.setReportType(null);
        record.setHostname(null);
        record.setComponent(null);
        record.setData(null);
        record.setForward_msg_to_splunk(null);
        record.setReporter(null);
        record.setEnv(null);
        record.setTimestamp(null);
        record.setHost(null);
        record.setSource(null);
        record.setSourceType(null);
        
        // Assert
        assertNull(record.getReportType());
        assertNull(record.getHostname());
        assertNull(record.getComponent());
        assertNull(record.getData());
        assertNull(record.getForward_msg_to_splunk());
        assertNull(record.getReporter());
        assertNull(record.getEnv());
        assertNull(record.getTimestamp());
        assertNull(record.getHost());
        assertNull(record.getSource());
        assertNull(record.getSourceType());
    }

    // ========================================================================
    // INTEGRATION TESTS
    // ========================================================================

    @Test
    void testCompleteWorkflow() {
        // Arrange & Act
        SplunkLogRecord record = new SplunkLogRecord();
        record.setReportType("INFO");
        record.setHostname("prod-server-01");
        record.setComponent("APIController");
        record.setForward_msg_to_splunk("true");
        record.setReporter("SpringBootApplication");
        record.setEnv("production");
        record.setTimestamp("2024-11-08 18:15:30.123");
        record.setHost("api.example.com");
        record.setSource("application-logs");
        record.setSourceType("json");
        
        SplunkEvent event = new SplunkEvent.SplunkEventBuilder("DefaultAPI", "production", "WebChannel", "TXN-12345")
                .transactionType(TransactionType.Response)
                .errorCode("ERR-000")
                .build();
        record.setData(event);
        
        // Assert
        assertEquals("INFO", record.getReportType());
        assertEquals("prod-server-01", record.getHostname());
        assertEquals("APIController", record.getComponent());
        assertEquals("true", record.getForward_msg_to_splunk());
        assertEquals("SpringBootApplication", record.getReporter());
        assertEquals("production", record.getEnv());
        assertEquals("2024-11-08 18:15:30.123", record.getTimestamp());
        assertEquals("api.example.com", record.getHost());
        assertEquals("application-logs", record.getSource());
        assertEquals("json", record.getSourceType());
        assertNotNull(record.getData());
    }

    @Test
    void testEmptyStringValues() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        
        // Act
        record.setReportType("");
        record.setHostname("");
        record.setComponent("");
        record.setForward_msg_to_splunk("");
        record.setReporter("");
        record.setEnv("");
        record.setTimestamp("");
        record.setHost("");
        record.setSource("");
        record.setSourceType("");
        
        // Assert
        assertEquals("", record.getReportType());
        assertEquals("", record.getHostname());
        assertEquals("", record.getComponent());
        assertEquals("", record.getForward_msg_to_splunk());
        assertEquals("", record.getReporter());
        assertEquals("", record.getEnv());
        assertEquals("", record.getTimestamp());
        assertEquals("", record.getHost());
        assertEquals("", record.getSource());
        assertEquals("", record.getSourceType());
    }

    @Test
    void testVariousReportTypes() {
        // Test different report types
        String[] reportTypes = {"INFO", "ERROR", "WARNING", "DEBUG", "FATAL"};
        
        for (String reportType : reportTypes) {
            SplunkLogRecord record = new SplunkLogRecord();
            record.setReportType(reportType);
            assertEquals(reportType, record.getReportType());
        }
    }

    @Test
    void testVariousEnvironments() {
        // Test different environments
        String[] environments = {"localhost", "development", "staging", "production", "qa"};
        
        for (String env : environments) {
            SplunkLogRecord record = new SplunkLogRecord();
            record.setEnv(env);
            assertEquals(env, record.getEnv());
        }
    }

    @Test
    void testVariousSourceTypes() {
        // Test different source types
        String[] sourceTypes = {"json", "xml", "text", "csv", "log4j"};
        
        for (String sourceType : sourceTypes) {
            SplunkLogRecord record = new SplunkLogRecord();
            record.setSourceType(sourceType);
            assertEquals(sourceType, record.getSourceType());
        }
    }

    @Test
    void testLongStringValues() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String longValue = "A".repeat(1000);
        
        // Act
        record.setReportType(longValue);
        record.setHostname(longValue);
        record.setComponent(longValue);
        
        // Assert
        assertEquals(longValue, record.getReportType());
        assertEquals(longValue, record.getHostname());
        assertEquals(longValue, record.getComponent());
    }

    @Test
    void testSpecialCharactersInFields() {
        // Arrange
        SplunkLogRecord record = new SplunkLogRecord();
        String specialChars = "Test @#$%^&*() <> {} [] | \\ / ? ~ `";
        
        // Act
        record.setComponent(specialChars);
        record.setReporter(specialChars);
        
        // Assert
        assertEquals(specialChars, record.getComponent());
        assertEquals(specialChars, record.getReporter());
    }

    @Test
    void testTimestampFormats() {
        // Test various timestamp formats
        String[] timestamps = {
            "2024-11-08 18:00:00.000",
            "2024-11-08T18:00:00Z",
            "1699464000000",
            "2024-11-08 18:00:00",
            "Nov 08, 2024 6:00:00 PM"
        };
        
        for (String timestamp : timestamps) {
            SplunkLogRecord record = new SplunkLogRecord();
            record.setTimestamp(timestamp);
            assertEquals(timestamp, record.getTimestamp());
        }
    }

    @Test
    void testMultipleSplunkEvents() {
        // Test setting different SplunkEvent objects
        SplunkLogRecord record = new SplunkLogRecord();
        
        // First event
        SplunkEvent event1 = new SplunkEvent.SplunkEventBuilder("API1", "dev", "ch1", "TXN-1")
                .transactionType(TransactionType.Request)
                .build();
        record.setData(event1);
        assertEquals(event1, record.getData());
        
        // Second event
        SplunkEvent event2 = new SplunkEvent.SplunkEventBuilder("API2", "prod", "ch2", "TXN-2")
                .transactionType(TransactionType.Response)
                .build();
        record.setData(event2);
        assertEquals(event2, record.getData());
    }
}
