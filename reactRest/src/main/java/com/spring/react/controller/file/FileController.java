package com.spring.react.controller.file;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    @Value("${file.root-dir}")
    private String rootDir;

    @GetMapping("/file/board/{boardId}/{fileName}")
    public ResponseEntity<Resource> downloadBoardFile(
            @PathVariable("boardId") int boardId,
            @PathVariable("fileName") String fileName
    ) {
        try {
            // ✅ 핵심: URL path로 들어온 fileName은 인코딩(%EC%...) 상태일 수 있음
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

            Path filePath = Paths.get(rootDir, "upload", "file", "board", String.valueOf(boardId), decodedFileName)
                                 .normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // ✅ 다운로드 파일명 헤더(한글 대응)
            // filename(구형) + filename*(RFC5987, UTF-8) 같이 주는 게 제일 안전
            String encoded = java.net.URLEncoder.encode(decodedFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + decodedFileName + "\"; filename*=UTF-8''" + encoded)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
