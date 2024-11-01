package com.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.springboot"})
public class SpringStartApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringStartApplication.class, args);
  }
}



