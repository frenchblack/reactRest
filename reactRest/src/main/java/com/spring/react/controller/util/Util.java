package com.spring.react.controller.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.react.service.board.BoardService;
import com.spring.react.vo.BoardVO;

@RestController
public class Util {
	@GetMapping("/chkLogin")
	public ResponseEntity<?> chkLogin() {
		return ResponseEntity.ok().build();  
	}
}
