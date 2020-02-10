package com.stone.controller;

import com.stone.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RestController
public class HelloController {

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
}
