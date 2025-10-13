package com.anr.controller;

import static com.anr.common.TestHelper.EN_US;
import static com.anr.common.TestHelper.SRC_CHANNEL01;
import static com.anr.common.TestHelper.URI_DEFSVC;
import static com.anr.common.TestHelper.getHttpHeaders;
import static com.anr.common.TestHelper.mockDefApi_scenario01;
import static com.anr.common.TestHelper.mockDefApi_scenario02;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class MainSBControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void test_defaultApi_validParameters() throws Exception {
        MockHttpServletRequestBuilder reqBldr = get(URI_DEFSVC);
        reqBldr.headers(getHttpHeaders("abc123", SRC_CHANNEL01, EN_US));
        reqBldr.params(mockDefApi_scenario01());

        mockMvc.perform(reqBldr).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void test_defaultApi_oneEmptyParameter() throws Exception {
        MockHttpServletRequestBuilder reqBldr = get(URI_DEFSVC);
        reqBldr.headers(getHttpHeaders("abc123", SRC_CHANNEL01, EN_US));
        reqBldr.params(mockDefApi_scenario02());

        mockMvc.perform(reqBldr).andDo(print()).andExpect(status().isOk());
    }
}
