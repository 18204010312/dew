package com.trc.test.web;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebTest {

    private Logger logger = LoggerFactory.getLogger(WebTest.class);

    @Autowired
    private TestRestTemplate testRestTemplate;

    public void testAll() throws Exception {
        testResponseFormat();
        testValidation();
        testTimeConvert();
    }

    private void testResponseFormat() throws Exception {
        Resp<String> result = Resp.generic(testRestTemplate.getForObject("/test/success?q=TEST", Resp.class), String.class);
        Assert.assertEquals("successful", result.getBody());
        result = Resp.generic(testRestTemplate.getForObject("/test/badRequest?q=TEST", Resp.class), String.class);
        Assert.assertEquals("badrequest", result.getMessage());
        ResponseEntity<Resp> resultEntity = testRestTemplate.getForEntity("/test/customError?q=TEST", Resp.class);
        Assert.assertEquals(500, resultEntity.getStatusCodeValue());
        Assert.assertEquals("A000", resultEntity.getBody().getCode());
        resultEntity = testRestTemplate.getForEntity("/test/customHttpState?q=TEST", Resp.class);
        Assert.assertEquals(401, resultEntity.getStatusCodeValue());
        Assert.assertEquals("A000", resultEntity.getBody().getCode());
    }

    private void testValidation() throws Exception {
        // group:create
        WebController.UserDTO userDTO = new WebController.UserDTO();
        userDTO.setAge(11);
        userDTO.setIdCard("110101201709013173");
        ResponseEntity<Resp> createResult = testRestTemplate.postForEntity("/test/valid-create", userDTO, Resp.class);
        Assert.assertEquals(200, createResult.getStatusCodeValue());
        Assert.assertEquals("400", createResult.getBody().getCode());
        Assert.assertTrue(createResult.getBody().getMessage().contains("Detail"));
        userDTO.setIdCard("110101201709013174");
        createResult = testRestTemplate.postForEntity("/test/valid-create", userDTO, Resp.class);
        Assert.assertEquals(200, createResult.getStatusCodeValue());
        Assert.assertEquals("400", createResult.getBody().getCode());
        Assert.assertTrue(createResult.getBody().getMessage().contains("Detail"));
        userDTO.setPhone("15971997041");
        ResponseEntity<WebController.UserDTO> createSuccessResult = testRestTemplate.postForEntity("/test/valid-create", userDTO, WebController.UserDTO.class);
        Assert.assertEquals(200, createSuccessResult.getStatusCodeValue());
        Assert.assertEquals("15971997041", createSuccessResult.getBody().getPhone());

        // group:update
        Map<String, Object> map = new HashMap<>();
        map.put("idCard", "110101201709013173");
        ResponseEntity<Resp> updateResult = testRestTemplate.postForEntity("/test/valid-update", map, Resp.class);
        Assert.assertEquals(200, updateResult.getStatusCodeValue());
        Assert.assertEquals("400", updateResult.getBody().getCode());
        Assert.assertTrue(updateResult.getBody().getMessage().contains("Detail"));
        map.put("idCard", "110101201709013174");
        ResponseEntity<WebController.UserDTO> updateSuccessResult = testRestTemplate.postForEntity("/test/valid-update", map, WebController.UserDTO.class);
        Assert.assertEquals(200, updateSuccessResult.getStatusCodeValue());
        Assert.assertEquals("110101201709013174", updateSuccessResult.getBody().getIdCard());

        // path
        ResponseEntity<Resp> pathResult = testRestTemplate.getForEntity("/test/valid-method-spring/1", Resp.class);
        Assert.assertEquals(200, pathResult.getStatusCodeValue());
        Assert.assertEquals("400", pathResult.getBody().getCode());
        Assert.assertTrue(pathResult.getBody().getMessage().contains("Detail"));
        pathResult = testRestTemplate.getForEntity("/test/valid-method-own/1865712020", Resp.class);
        Assert.assertEquals(200, pathResult.getStatusCodeValue());
        Assert.assertEquals("400", pathResult.getBody().getCode());
        Assert.assertTrue(pathResult.getBody().getMessage().contains("Detail"));

        // error mapping
        ResponseEntity<Resp> mappingResult = testRestTemplate.getForEntity("/test/error-mapping", Resp.class);
        Assert.assertEquals(401, mappingResult.getStatusCodeValue());
        Assert.assertEquals("x00010", mappingResult.getBody().getCode());
        Assert.assertEquals("[Internal Server Error]认证错误", mappingResult.getBody().getMessage());
        logger.info($.json.toJsonString(mappingResult.getBody()));
    }

    private void testTimeConvert() throws IOException {
        Resp timeResult = testRestTemplate.getForObject("/test/time/param?date-time=2013-07-02 17:39:00&date=2013-07-02&time=17:39:12&instant=1509430693548", Resp.class);
        Assert.assertTrue(timeResult.ok());
        WebController.TimeDTO timeDTO = new WebController.TimeDTO();
        timeDTO.setLocalDate(LocalDate.now());
        timeDTO.setLocalTime(LocalTime.now());
        timeDTO.setLocalDateTime(LocalDateTime.now());
        timeResult = testRestTemplate.postForObject("/test/time/body", timeDTO, Resp.class);
        Assert.assertTrue(timeResult.ok());
    }

}
