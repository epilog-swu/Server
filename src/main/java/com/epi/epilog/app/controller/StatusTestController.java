package com.epi.epilog.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class StatusTestController {
    /**
     * elastic beanstalk health check
     * @return
     */
    @GetMapping("/test")
    public ResponseEntity<String> testAPI(){
        return ResponseEntity.ok("successful");
    }

    /**
     * spring security test
     * @return
     */
    @GetMapping("/auth/test")
    public ResponseEntity<String> testAuthAPI(){
        return ResponseEntity.ok("successful");
    }
}
