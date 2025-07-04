package com.spring.react.service.board;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.multipart.MultipartFile;

import com.spring.react.mapper.board.BoardMapper;
import com.spring.react.vo.BoardVO;

@Service
public class BoardService {

    private final CorsConfigurationSource corsConfigurationSource;
    private final String TEMP_DIR = "/images/temp/";
    private final String FINAL_DIR = "/images/board/";
	
	@Autowired
	public BoardMapper mapper;

    BoardService(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }
	
	public Map<String, Object> getBoardList(int page,int maxNext, int size, String keyword, String type, String category, String subCategory, String menu_cd, String sort, String period) {
		int offset = page * size;
		List<BoardVO> list = mapper.getBoardList(offset, size, keyword, type, category , subCategory, menu_cd, sort, period); 

		//페이징
	    int nextCount = 0;
	    for (int i = 1; i <= maxNext; i++) {
	        int nextOffset = (page + i) * size;
	        List<BoardVO> temp = mapper.getBoardList(nextOffset, size, keyword, type, category , subCategory, menu_cd, sort, period);
	        if (temp == null || temp.isEmpty()) break;
	        nextCount++;
	    }
		
	    Map<String, Object> result = new HashMap<>();
	    result.put("content", list);
	    result.put("nextCount", nextCount);
	    return result;
	}

	public int postBoard(BoardVO boardVo) {
		boardVo.setEx_image(is_image(boardVo.getContent()));
		boardVo.setEx_file(is_file(boardVo.getContent()));
		mapper.postBoard(boardVo);
		if(replaceTempPath(boardVo))updateContent(boardVo);
	    return boardVo.getBoard_no();
	}

	public BoardVO viewBoard(int board_no) {
		return mapper.viewBoard(board_no);
	}

	public boolean is_image(String content) {
	    if (content == null || content.isEmpty()) return false;
	    
	    Pattern pattern = Pattern.compile("<img[^>]+src=[\"'][^\"'>]*\\/images\\/[^\"'>]+[\"']", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(content);
	    
	    return matcher.find();
	}
	
	public boolean is_file(String content) {
	    if (content == null || content.isEmpty()) return false;
	    
	    return false;
	}
	
	//조회수 ++
	public int increaseViewCnt(int board_no) {
		return mapper.increaseViewCnt(board_no);
	}

	//글작성 중 파일 임시 저장.
	public String saveTempFile(String uuid, MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String dirPath = "upload/images/temp/" + uuid;
        String filePath = dirPath + "/" + fileName;

        try {
            Files.createDirectories(Paths.get(dirPath));
            file.transferTo(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "/images/temp/" + uuid + "/" + fileName;
		
	}
	
    public Boolean replaceTempPath(BoardVO boardVo) {
        if (boardVo.getContent() == null) return false;
        if (!boardVo.isEx_image())return false;

        String tempPath = TEMP_DIR + boardVo.getUuid() + "/";
        String finalPath = FINAL_DIR + boardVo.getBoard_no() + "/";

        // 실제 파일 이동 로직
        Path sourceDir = Paths.get("upload" + tempPath);
        Path targetDir = Paths.get("upload" + finalPath);

        try {
            if (Files.exists(sourceDir)) {
                Files.createDirectories(targetDir);
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
                    for (Path file : stream) {
                        Files.move(file, targetDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                Files.delete(sourceDir); // temp 폴더 삭제
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 본문 내 이미지 경로 치환
        boardVo.setContent(boardVo.getContent().replace(tempPath, finalPath));
        
        return true;
    }

    public void updateContent(BoardVO boardVo) {
        mapper.updateContent(boardVo.getBoard_no(), boardVo.getContent());
    }
}
