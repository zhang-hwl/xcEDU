package com.xuecheng.manage_cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RestTemplateTest {

    @Autowired
    RestTemplate restTemplate;


    @Test
    public void testRestTemplate(){
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/get/5a791725dd573c3574ee333f", Map.class);
        Map body = forEntity.getBody();
        System.out.println(body);
    }















}
