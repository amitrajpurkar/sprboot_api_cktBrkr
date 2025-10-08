package com.anr.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

public class TestHelper {

    public static final String URI_DEFSVC = "/api/v1/default";
    public static final String SRC_CHANNEL01 = "Allowed-Channel";
    public static final String SRC_CHANNEL02 = "NotAllowed-Channel";
    public static final String EN_US = "EN_US";
    public static final String ES_US = "ES_US";

    public static LocalDateTime convertInputDateString(String inputDate) {
        LocalDateTime ldt = null;
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = Instant.parse(inputDate);
        ldt = LocalDateTime.ofInstant(instant, ZoneId.of(defaultZoneId.getId()));
        return ldt;
    }

    public static LocalDateTime convertISODateStringToDate(String isoDateString) {
        return LocalDateTime.parse(isoDateString, DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss zzz yyyy"));
    }

    public static String getJsonXmlStr(String inputFile) {
        File file;
        try {
            file = ResourceUtils.getFile(inputFile);
            return new String(Files.readAllBytes(file.toPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MultiValueMap<String, String> mockReParamsForDefaultApi(String field1Val, String field2Val) {
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();

        paramsMap.add("field1", field1Val);
        paramsMap.add("field2", field2Val);

        return paramsMap;
    }

    public static HttpHeaders getHttpHeaders(String transactionID, String sourceChannel, String locale) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("transactionID", transactionID);
        headers.add("sourceChannel", sourceChannel);
        headers.add("locale", locale);
        return headers;
    }

    /**
     * create mock data for different request scenarios
     */

    public static MultiValueMap<String, String> mockDefApi_scenario01() {
        return mockReParamsForDefaultApi("field1-value", "field2-value");
    }

    public static MultiValueMap<String, String> mockDefApi_scenario02() {
        return mockReParamsForDefaultApi("", "field2-value");
    }

    public static MultiValueMap<String, String> mockDefApi_scenario03() {
        return mockReParamsForDefaultApi(null, "field2-value");
    }

    public static MultiValueMap<String, String> mockDefApi_scenario04() {
        return mockReParamsForDefaultApi("", "");
    }

    public static MultiValueMap<String, String> mockDefApi_scenario05() {
        return mockReParamsForDefaultApi(null, null);
    }
}
