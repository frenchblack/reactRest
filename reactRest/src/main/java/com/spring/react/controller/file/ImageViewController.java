package com.spring.react.controller.file;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


//*************************************************
//   WEBCONFIG에서 하니까 한글명파일은 깨져서 컨트롤러 추가
//*************************************************

@RestController
public class ImageViewController {

    @Value("${file.root-dir}")
    private String rootDir;

    @GetMapping("/images/temp/{uuid}/{fileName:.+}")
    public ResponseEntity<Resource> viewTemp(
            @PathVariable("uuid") String uuid,
            @PathVariable("fileName") String fileName
    ) throws Exception {

        String decoded = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

        Path path = Paths.get(rootDir, "upload", "images", "temp", uuid, decoded).normalize();
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
			contentType = "application/octet-stream";
		}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @GetMapping("/images/board/{boardId}/{fileName:.+}")
    public ResponseEntity<Resource> viewBoard(
            @PathVariable("boardId") int boardId,
            @PathVariable("fileName") String fileName
    ) throws Exception {

        String decoded = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

        Path path = Paths.get(rootDir, "upload", "images", "board", String.valueOf(boardId), decoded).normalize();
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
			contentType = "application/octet-stream";
		}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
    @GetMapping("/images/thumb/{boardId}/{fileName:.+}")
    public ResponseEntity<Resource> viewThumb(
            @PathVariable("boardId") int boardId,
            @PathVariable("fileName") String fileName
    ) throws Exception {

        String decoded = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

        Path path = Paths.get(rootDir, "upload", "images", "thumb",
                String.valueOf(boardId), decoded).normalize();

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
}