package com.epi.epilog.app.controller;

import com.epi.epilog.app.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public ResponseEntity<String> testAPI(){
        return ResponseEntity.ok("successful");
    }
    @GetMapping("/auth/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> testAuthAPI(){
        return ResponseEntity.ok("successful");
    }
}
