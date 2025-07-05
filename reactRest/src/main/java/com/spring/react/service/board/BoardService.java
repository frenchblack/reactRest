package com.spring.react.service.board;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.multipart.MultipartFile;
import com.spring.react.config.JwtTokenUtil;
import com.spring.react.mapper.board.BoardMapper;
import com.spring.react.vo.BoardVO;
import com.spring.react.vo.FileVO;

@Service
public class BoardService {

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${file.root-dir}")
    private String rootDir;
	
    private final CorsConfigurationSource corsConfigurationSource;
    private final String TEMP_DIR = "/images/temp/";
    private final String FINAL_DIR = "/images/board/";
    private final String FILE_DIR = "/file/board/";
	
	@Autowired
	public BoardMapper mapper;

    BoardService(CorsConfigurationSource corsConfigurationSource, JwtTokenUtil jwtTokenUtil) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtTokenUtil = jwtTokenUtil;
    }
	
    //게시판 목록 조회
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

	//게시판 저장
	public int postBoard(BoardVO boardVo, List<MultipartFile> files) {
		//파일여부 이미지여부 컬럼 셋팅
		boardVo.setEx_image(is_image(boardVo.getContent()));
		boardVo.setEx_file(files != null ? true : false);

		//board insert
		mapper.postBoard(boardVo);
		//image path change
		if(replaceTempPath(boardVo))updateContent(boardVo);
		
		//파일저장.
		if(boardVo.isEx_file()) saveFileMuti(saveFileToDisk(files, boardVo.getBoard_no()));
		
		
		
	    return boardVo.getBoard_no();
	}

	//상세보기
	public BoardVO viewBoard(int board_no) {
		return mapper.viewBoard(board_no);
	}
	
	//조회수 ++
	public int increaseViewCnt(int board_no) {
		return mapper.increaseViewCnt(board_no);
	}

	//이미지 여부 확인
	public boolean is_image(String content) {
	    if (content == null || content.isEmpty()) return false;
	    
	    Pattern pattern = Pattern.compile("<img[^>]+src=[\"'][^\"'>]*\\/images\\/[^\"'>]+[\"']", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(content);
	    
	    return matcher.find();
	}
	
	//파일 여부 확인
	public boolean is_file(String content) {
	    if (content == null || content.isEmpty()) return false;
	    
	    return false;
	}
	

	//글작성 중 파일 임시 저장.
	public String saveTempFile(String uuid, MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String dirPath = rootDir + "/upload/images/temp/" + uuid;
        String filePath = dirPath + "/" + fileName;

        try {
            Files.createDirectories(Paths.get(dirPath));
            file.transferTo(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "/images/temp/" + uuid + "/" + fileName;
		
	}
	
	//글 저장시 임시파일에 있던 이미지 저장경로로 변경
    public Boolean replaceTempPath(BoardVO boardVo) {
        if (boardVo.getContent() == null) return false;
        if (!boardVo.isEx_image())return false;

        String tempPath = TEMP_DIR + boardVo.getUuid() + "/";
        String finalPath = FINAL_DIR + boardVo.getBoard_no() + "/";

        // 실제 파일 이동 로직
        Path sourceDir = Paths.get(rootDir + "/upload" + tempPath);
        Path targetDir = Paths.get(rootDir + "/upload" + finalPath);

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

    //게시판 conetent 수정
    public int updateContent(BoardVO boardVo) {
        return mapper.updateContent(boardVo.getBoard_no(), boardVo.getContent());
    }
    
    //파일 물리적 저장 
    public List<FileVO> saveFileToDisk(List<MultipartFile> files, int board_no) {
    	if(files == null) return null;
    	List<FileVO> fileList = new ArrayList<>();
    	
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) continue;

            // 저장 이름 생성 (i_나노초_원본명)
            String originNm = file.getOriginalFilename();
            String savedNm = i + "_" + System.nanoTime() + "_" + originNm;

            // 저장 경로
            String filePath = FILE_DIR + board_no + "/" +  savedNm;
            File targetFile = new File(rootDir + "/upload" + filePath);
            File parentDir = targetFile.getParentFile();
            System.out.println("parentDir : " + parentDir);
            System.out.println("parentDir : " + !parentDir.exists());
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            try {
                // 물리적 저장
                file.transferTo(targetFile);

                // VO 구성
                FileVO fileVo = new FileVO();
                fileVo.setBoard_no(board_no);
                fileVo.setOrigin_nm(originNm);
                fileVo.setSaved_nm(savedNm);
                fileVo.setFile_path(filePath);
                fileVo.setFile_size(file.getSize());
                fileVo.setFile_type(file.getContentType());
                
                fileList.add(fileVo);

            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패: " + originNm, e);
            }
        }
        return fileList;
    }
    public int saveFileMuti(List<FileVO> fileVo) {
    	if(fileVo == null) return 0;
    	return mapper.saveFileMuti(fileVo);
    }

	public List<FileVO> getFileList(int board_no) {
		return mapper.getFileList(board_no);
	}
}
