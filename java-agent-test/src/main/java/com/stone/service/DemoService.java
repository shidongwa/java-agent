package com.stone.service;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class DemoService {

    public String sayHello(String name) {
        LocalDateTime dateTime = LocalDateTime.now(Clock.system(ZoneId.systemDefault()));
        String hello = String.format("Hello %s, Now is %s", name, dateTime);

        return hello;
    }
}
