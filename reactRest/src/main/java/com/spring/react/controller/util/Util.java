package com.spring.react.controller.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Util {
	@GetMapping("/chkLogin")
	public ResponseEntity<?> chkLogin() {
		return ResponseEntity.ok().build();
	}
}
