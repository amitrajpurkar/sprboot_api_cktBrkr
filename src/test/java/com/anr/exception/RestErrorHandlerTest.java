package com.anr.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Comprehensive test suite for RestErrorHandler class
 * Target: >90% code coverage
 * 
 * @author amitr
 */
@ExtendWith(MockitoExtension.class)
public class RestErrorHandlerTest {

    private RestErrorHandler errorHandler;

    @Mock
    private ClientHttpResponse mockResponse;

    @BeforeEach
    void setUp() {
        errorHandler = new RestErrorHandler();
    }

    // ========================================================================
    // HANDLE ERROR TESTS
    // ========================================================================

    @Test
    void testHandleError_with400BadRequest() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn("Bad Request");
        when(mockResponse.getBody()).thenReturn(createInputStream("Invalid request"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_with401Unauthorized() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(mockResponse.getStatusText()).thenReturn("Unauthorized");
        when(mockResponse.getBody()).thenReturn(createInputStream("Authentication required"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_with403Forbidden() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
        when(mockResponse.getStatusText()).thenReturn("Forbidden");
        when(mockResponse.getBody()).thenReturn(createInputStream("Access denied"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_with404NotFound() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(mockResponse.getStatusText()).thenReturn("Not Found");
        when(mockResponse.getBody()).thenReturn(createInputStream("Resource not found"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_with500InternalServerError() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(mockResponse.getStatusText()).thenReturn("Internal Server Error");
        when(mockResponse.getBody()).thenReturn(createInputStream("Server error occurred"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_with502BadGateway() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);
        when(mockResponse.getStatusText()).thenReturn("Bad Gateway");
        when(mockResponse.getBody()).thenReturn(createInputStream("Gateway error"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_with503ServiceUnavailable() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
        when(mockResponse.getStatusText()).thenReturn("Service Unavailable");
        when(mockResponse.getBody()).thenReturn(createInputStream("Service temporarily unavailable"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_with504GatewayTimeout() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.GATEWAY_TIMEOUT);
        when(mockResponse.getStatusText()).thenReturn("Gateway Timeout");
        when(mockResponse.getBody()).thenReturn(createInputStream("Request timeout"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    // ========================================================================
    // RESPONSE BODY TESTS
    // ========================================================================

    @Test
    void testHandleError_withEmptyResponseBody() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn("Bad Request");
        when(mockResponse.getBody()).thenReturn(createInputStream(""));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_withJsonResponseBody() throws IOException {
        // Arrange
        String jsonBody = "{\"error\":\"Invalid request\",\"code\":\"ERR-001\"}";
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn("Bad Request");
        when(mockResponse.getBody()).thenReturn(createInputStream(jsonBody));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_withXmlResponseBody() throws IOException {
        // Arrange
        String xmlBody = "<error><message>Invalid request</message></error>";
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn("Bad Request");
        when(mockResponse.getBody()).thenReturn(createInputStream(xmlBody));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_withLargeResponseBody() throws IOException {
        // Arrange
        StringBuilder largeBody = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeBody.append("Error message line ").append(i).append("\n");
        }
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(mockResponse.getStatusText()).thenReturn("Internal Server Error");
        when(mockResponse.getBody()).thenReturn(createInputStream(largeBody.toString()));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    // ========================================================================
    // EDGE CASE TESTS
    // ========================================================================

    @Test
    void testHandleError_withNullStatusText() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn(null);
        when(mockResponse.getBody()).thenReturn(createInputStream("Error"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_withEmptyStatusText() throws IOException {
        // Arrange
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn("");
        when(mockResponse.getBody()).thenReturn(createInputStream("Error"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_withCustomStatusCode() throws IOException {
        // Arrange
        HttpStatusCode customStatus = HttpStatusCode.valueOf(418); // I'm a teapot
        when(mockResponse.getStatusCode()).thenReturn(customStatus);
        when(mockResponse.getStatusText()).thenReturn("I'm a teapot");
        when(mockResponse.getBody()).thenReturn(createInputStream("Custom error"));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getStatusCode();
        verify(mockResponse, times(1)).getStatusText();
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_multipleCallsWithDifferentErrors() throws IOException {
        // Test 1: 400 Bad Request
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn("Bad Request");
        when(mockResponse.getBody()).thenReturn(createInputStream("Error 1"));
        errorHandler.handleError(mockResponse);
        
        // Test 2: 500 Internal Server Error
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(mockResponse.getStatusText()).thenReturn("Internal Server Error");
        when(mockResponse.getBody()).thenReturn(createInputStream("Error 2"));
        errorHandler.handleError(mockResponse);
        
        // Assert - verify both calls were made
        verify(mockResponse, times(2)).getStatusCode();
        verify(mockResponse, times(2)).getStatusText();
        verify(mockResponse, times(2)).getBody();
    }

    // ========================================================================
    // INHERITANCE TESTS
    // ========================================================================

    @Test
    void testInheritanceFromDefaultResponseErrorHandler() {
        // Assert
        assertTrue(errorHandler instanceof org.springframework.web.client.DefaultResponseErrorHandler);
    }

    // ========================================================================
    // SPECIAL CHARACTER TESTS
    // ========================================================================

    @Test
    void testHandleError_withSpecialCharactersInBody() throws IOException {
        // Arrange
        String bodyWithSpecialChars = "Error: Invalid input @#$%^&*() <> {} [] | \\ / ? ~ `";
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn("Bad Request");
        when(mockResponse.getBody()).thenReturn(createInputStream(bodyWithSpecialChars));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_withUnicodeCharacters() throws IOException {
        // Arrange
        String unicodeBody = "错误: 无效的请求 (Error: Invalid request)";
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn("Bad Request");
        when(mockResponse.getBody()).thenReturn(createInputStream(unicodeBody));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getBody();
    }

    @Test
    void testHandleError_withMultilineBody() throws IOException {
        // Arrange
        String multilineBody = "Error occurred:\nLine 1: Validation failed\nLine 2: Field 'name' is required";
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn("Bad Request");
        when(mockResponse.getBody()).thenReturn(createInputStream(multilineBody));
        
        // Act
        errorHandler.handleError(mockResponse);
        
        // Assert
        verify(mockResponse, times(1)).getBody();
    }

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    private InputStream createInputStream(String content) {
        return new ByteArrayInputStream(content.getBytes());
    }
}
