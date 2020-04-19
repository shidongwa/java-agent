package com.stone.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DemoServiceTest {

    @Autowired
    private DemoService service;

    @Test
    public void sayHello() {
        String hello = service.sayHello("Stark");
        System.out.println(hello);
        assertEquals("Hello Stark",  hello.substring(0, 11));
    }
}