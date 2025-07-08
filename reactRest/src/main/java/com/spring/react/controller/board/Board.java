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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.multipart.MultipartFile;

import com.spring.react.service.board.BoardService;
import com.spring.react.vo.BoardVO;
import com.spring.react.vo.FileVO;
import com.spring.react.vo.UserVO;

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

    //게시글 리스트 조회
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
	
	//게시글 생성
	@PostMapping("/postBoard")
	public int postBoard( @RequestPart("data") BoardVO boardVo
						, @RequestPart(value = "files", required = false) List<MultipartFile> files) {
		int boaordNo = boardService.postBoard(boardVo, files);
		return boaordNo;
	}

	//게시글 상세조회
	@GetMapping("/viewBoard")
	public Map<String, Object> viewBoard(@RequestParam int board_no) {
		boardService.increaseViewCnt(board_no);
		Map<String, Object>result = boardService.getDetailBoardItems(board_no);
		
	    return result;
	}
	
	//조회수 증가하지 않는 조회
	@GetMapping("/getBoradDtail")
	public Map<String, Object> getBoradDtail(@RequestParam int board_no) {		
	    return boardService.getDetailBoardItems(board_no);
	}
	
	//게시글 업데이트
	@PutMapping("/updateBoard")
	public int updateBoard( @RequestPart("data") BoardVO boardVo
						  , @RequestPart(value = "files", required = false) List<MultipartFile> files
						  , @RequestPart(value = "deleteFiles", required = false) List<FileVO> delFileList
						  , @AuthenticationPrincipal UserVO user) {
	    System.out.println("📥 제목: {}" + boardVo.getTitle() +" : " +  boardVo.getBoard_no());
	    System.out.println("📥 파일 수: {}" + (files != null ? files.size() : 0));
	    System.out.println("📥 삭제파일 수: {}" + (delFileList != null ? delFileList.size() : 0));
	    System.out.println("📥 Token user getUsername : " + user.getUsername());
	    
	    //작성자 일치여부 확인.
	    if (!boardService.isWriter(boardVo.getBoard_no(), user.getUsername())) {
	    	return -1;
	    }
	    
		return boardService.updateBoard(boardVo, files , delFileList);
//		return 0 ;
	}	
	
	//게시글 삭제
	@DeleteMapping("/deleteBoard")
	public int deleteBoard( @RequestParam int board_no, @AuthenticationPrincipal UserVO user) {
		System.out.println("board_no : " + board_no);
	    //작성자 일치여부 확인.
	    if (!boardService.isWriter(board_no, user.getUsername())) {
	    	System.out.println("작성자 일치하지 않음 : " +  user.getUsername());
	    	return -1;
	    }
	    
		return boardService.deleteBoard(board_no);
	}
	
	//글작성 중 이미지 임시 저장
    @PatchMapping("/boadUpload/temp/{uuid}")
    public Map<String, Object> uploadTempFile(@PathVariable String uuid, @RequestParam("file") MultipartFile file) {
        String savedPath = boardService.saveTempFile(uuid, file);
        Map<String, Object> result = new HashMap<>();
        result.put("url", savedPath);
        return result;
    }
	
}
