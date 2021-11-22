package com.stone.controller;

import com.stone.domain.User;
import com.stone.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RestController
public class HelloController {
    @Autowired
    private DemoService demoService;

    @RequestMapping("/")
    public String index() {
        return String.format("Greetings from Java Agent - %s !",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    @PostMapping(value = "/demo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> formSubmit(@RequestBody MultiValueMap<String, String> formParams) {
        System.out.println("formParams = [" + formParams + "]");
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>("SUCCESS!", HttpStatus.OK);
    }

    @PostMapping(value = "/user")
    User newEmployee(@RequestBody User user) {

        return user;
    }

    @GetMapping(value = "/hello/{name}")
    public String sayHello(@PathVariable("name") String name) {
        String hello = demoService.sayHello(name);

        return hello;
    }
}
