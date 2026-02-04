package com.spring.react.service.board;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.react.mapper.board.BoardMapper;
import com.spring.react.service.common.OwnershipService;
import com.spring.react.vo.BoardVO;
import com.spring.react.vo.FileVO;

@Service
public class BoardService {

    @Value("${file.root-dir}")
    private String rootDir;
	
    private final String TEMP_DIR = "/images/temp/";
    private final String FINAL_DIR = "/images/board/";
    private final String FILE_DIR = "/file/board/";
    private final String IMAGE_PATTERN = "<img[^>]+src=[\"']((?:https?:\\/\\/[^\"'>]+)?\\/images\\/[^\"'>]+)[\"']";
	
	@Autowired
	public BoardMapper mapper;
	
	@Autowired
	private OwnershipService ownershipService;
    
    //=============================================================================================================================
    //=============================================================================================================================
    // BOARD_CRUD
    //=============================================================================================================================
    //=============================================================================================================================
    
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

	//board 상세 조회로직
	public Map<String, Object> getDetailBoardItems(@RequestParam int board_no){
		Map<String, Object>result = new HashMap<>();
		BoardVO board_vo = viewBoard(board_no);
		List<FileVO> fileList = getFileList(board_no);
		result.put("board", board_vo);
		result.put("file", fileList);
		
	    return result;
	}
	
	//상세보기
	public BoardVO viewBoard(int board_no) {
		return mapper.viewBoard(board_no);
	}
	
    //게시판 conetent 수정
    public int updateContent(BoardVO boardVo) {
        return mapper.updateContent(boardVo.getBoard_no(), boardVo.getContent());
    }

    //게시판 upadate
	public int updateBoard(BoardVO boardVo, List<MultipartFile> files, List<FileVO> delFileList) {
		boardVo.setEx_image(is_image(boardVo.getContent()));
		boardVo.setEx_file(is_file(boardVo.getBoard_no(), (files != null) ? files.size() : 0, (delFileList != null) ? delFileList.size() : 0));
		
		mapper.updateBoard(boardVo);
		
		deleteFileFromDisk(delFileList);
		deleteNotUsedImage(boardVo.getBoard_no(), boardVo.getContent());
		
		//image path change
		if(replaceTempPath(boardVo))updateContent(boardVo);
		
		//파일저장.
		if(boardVo.isEx_file()) saveFileMuti(saveFileToDisk(files, boardVo.getBoard_no()));
		
		return boardVo.getBoard_no();
	}

	public int deleteBoard(int board_no) {
		return mapper.deleteBoard(board_no);
	}
	
	//=============================================================================================================================
	//=============================================================================================================================
	// 파일처리 CRUD
	//=============================================================================================================================
	//=============================================================================================================================
	//파일 삭제
	public void deleteFileFromDisk(List<FileVO> delFileList) {
	    if (delFileList != null && !delFileList.isEmpty()) {
	        for (FileVO file : delFileList) {
	        	deleteFile(file.getFile_id());
	            String filePath = file.getFile_path();
	            if (filePath != null) new File(rootDir + "/upload" + filePath).delete();
	        }
	    }
	}
	
	//db에서 파일 삭제
	public void deleteFile(int file_id) {
		mapper.deleteFile(file_id);
	}
	
	//한글파일명 때문에 디코딩해서 빼주는데 여기는 디코딩을 해주지 않았기떄문에 이름비교시 다른이름으로 분류가 돼서 있는 파일이 삭제됐었음.
	//사용하지 않는 이미지 삭제
	public void deleteNotUsedImage(int boardId, String contentHtml) {
	    // 1. 본문에서 사용 중인 이미지 경로 추출 (절대경로 형태로 반환됨)
	    Set<String> usedImagePaths = extractImageSrcs(contentHtml);

	    // 2. 해당 게시글의 이미지가 저장된 실제 경로
	    Path boardImageDir = Paths.get(rootDir, "upload", "images", "board", String.valueOf(boardId));

	    File[] files = boardImageDir.toFile().listFiles();
	    if (files == null) {
	    	System.out.println("존재하는 이미지가 없습니다.");
	    	return;
	    } else {
	    	System.out.println("usedImagePaths : " + usedImagePaths);
	    }

	    for (File file : files) {
	    	String filePath = file.toPath().normalize().toString();
	    	
	        System.out.println("file.getAbsolutePath() : " + file.getAbsolutePath());
	        // 3. 사용되지 않은 이미지면 삭제
	        if (!usedImagePaths.contains(filePath)) {
	            file.delete();
	        }
	    }
	    
	    if (boardImageDir.toFile().listFiles().length == 0) {
	        boardImageDir.toFile().delete();
	    }
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
    
    //파일리스트 한번에 저장
    public int saveFileMuti(List<FileVO> fileVo) {
    	if(fileVo == null) return 0;
    	return mapper.saveFileMuti(fileVo);
    }

    //db에서 파일목록 조회
	public List<FileVO> getFileList(int board_no) {
		return mapper.getFileList(board_no);
	}

	//=============================================================================================================================
	//=============================================================================================================================
	// 기타 함수
	//=============================================================================================================================
	//=============================================================================================================================
	//이미지 여부 확인
	public boolean is_image(String content) {
	    if (content == null || content.isEmpty()) return false;
	    
	    Pattern pattern = Pattern.compile(IMAGE_PATTERN, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(content);
	    
	    return matcher.find();
	}
	
	//파일 여부 확인
	public boolean is_file(int board_no, int fileCnt, int delFileCnt) {
		boolean result = false;
		int currentFileCnt = getFileCnt(board_no);

		if((currentFileCnt + fileCnt) - delFileCnt > 0 ) result = true;
	    return result;
	}
	
	//본문에서 이미지 추출
	public Set<String> extractImageSrcs(String html) {
	    Set<String> srcSet = new HashSet<>();
	    Matcher matcher = Pattern.compile(IMAGE_PATTERN).matcher(html);
	    while (matcher.find()) {
	        String src = matcher.group(1);
	        
	     // ✅ 핵심: URL 인코딩(%EC%...)을 실제 한글 파일명으로 복원
	        String decodedSrc = URLDecoder.decode(src, StandardCharsets.UTF_8);	        

	        String absolutePath = convertToAbsolutePath(decodedSrc); // 구현 필요
	        srcSet.add(Paths.get(absolutePath).normalize().toString());
	    }
	    return srcSet;
	}
	
	//이미지 경로 변환
	public String convertToAbsolutePath(String webPath) {
//		String imageRelativePath = webPath.replaceFirst("^https?://[^/]+", ""); // 도메인 제거
		
		String relative = webPath.replaceFirst("^https?://[^/]+", "");
		
	    // ✅ 앞의 '/' 제거해서 Paths.get(rootDir, "upload", ...)가 정상적으로 결합되게
	    if (relative.startsWith("/")) {
	        relative = relative.substring(1);
	    }

	    // ✅ rootDir/upload/.... 로 안전 결합
	    Path p = Paths.get(rootDir, "upload", relative).normalize();
	    return p.toString();
	    
//		return Paths.get(rootDir, "/upload", imageRelativePath).toString();
	}
	
	//게시글 내 파일 갯수 반환
	public int getFileCnt(int board_no) {
		return mapper.getFileCnt(board_no);
	}
	
	//조회수 ++
	public int increaseViewCnt(int board_no) {
		return mapper.increaseViewCnt(board_no);
	}
	
	//글 작성자와 토큰의 유저가 같은 유저인지 확인.
	public boolean isWriter(int board_no, String user_id) {
		return ownershipService.isBoardOwner(board_no, user_id);
	}

}
