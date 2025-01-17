package com.springboot.health;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/health")
    public ResponseEntity getHello() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
