package com.spring.react.controller.board;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.multipart.MultipartFile;

import com.spring.react.service.board.BoardService;
import com.spring.react.vo.BoardVO;
import com.spring.react.vo.FileVO;

@RestController
public class Board {

    private final CorsConfigurationSource corsConfigurationSource;
    @Value("${file.root-dir}")
    private String rootDir;
    
	@Autowired
	public BoardService boardService;

    Board(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

	@GetMapping("/getBoardList")
	public Map<String, Object> getBoardList(@RequestParam int page
			, @RequestParam(defaultValue = "5") int maxNext
			, @RequestParam(defaultValue = "10") int size
			, @RequestParam(required = false) String keyword
		    , @RequestParam(required = false) String type
		    , @RequestParam(required = false) String category
		    , @RequestParam(required = false) String subCategory
		    , @RequestParam(required = false) String sort
		    , @RequestParam(required = false) String period
		    , String menu_cd) {
	    return boardService.getBoardList(page, maxNext, size, keyword, type, category , subCategory, menu_cd, sort, period);
	}
	
	@PostMapping("/postBoard")
	public int postBoard( @RequestPart("data") BoardVO boardVo
						, @RequestPart(value = "files", required = false) List<MultipartFile> files) {
	    System.out.println("📥 제목: {}" + boardVo.getTitle());
	    System.out.println("📥 파일 수: {}" + (files != null ? files.size() : 0));
		int boaordNo = boardService.postBoard(boardVo, files);
		return boaordNo;
	}
	
	@GetMapping("/viewBoard")
	public Map<String, Object> viewBoard(@RequestParam int board_no) {
		Map<String, Object>result = new HashMap<>();
		boardService.increaseViewCnt(board_no);
		BoardVO board_vo = boardService.viewBoard(board_no);
		List<FileVO> fileList = boardService.getFileList(board_no);
		result.put("board", board_vo);
		result.put("file", fileList);
		
	    return result;
	}
	
    @PatchMapping("/boadUpload/temp/{uuid}")
    public Map<String, Object> uploadTempFile(@PathVariable String uuid, @RequestParam("file") MultipartFile file) {
        String savedPath = boardService.saveTempFile(uuid, file);
        Map<String, Object> result = new HashMap<>();
        result.put("url", savedPath);
        return result;
    }
	
}
