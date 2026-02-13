package com.spring.react.scheduler;

import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TempFileCleanupScheduler {
    @Value("${file.root-dir}")
    private String rootDir;

    @Value("${file.cleanup.expire-hours}")
    private int expireHours;

    // 매시간 정각마다 실행
    @Scheduled(cron = "0 * * * * *")
    public void cleanupTempFiles() {
        // 루트 + 상대 경로 조립
        String tempDirPath = Paths.get(rootDir, "upload", "images", "temp").toString();

        File tempRoot = new File(tempDirPath);
        if (!tempRoot.exists() || !tempRoot.isDirectory()) {
            System.out.println("× 경로 없음 또는 디렉토리 아님: " + tempDirPath);
            return;
        }

        Instant cutoff = Instant.now().minus(expireHours, ChronoUnit.HOURS);
        deleteOldFiles(tempRoot, cutoff); // 재귀 삭제 호출
    }

    private void deleteOldFiles(File dir, Instant cutoff) {
        if (!dir.exists()) {
			return;
		}

        File[] contents = dir.listFiles();
        if (contents == null) {
			return;
		}

        for (File file : contents) {
            if (file.isDirectory()) {
                deleteOldFiles(file, cutoff); // 재귀적으로 처리
                // 하위 다 삭제하고 비었으면 디렉토리도 삭제
                if (file.listFiles() != null && file.listFiles().length == 0) {
                    boolean deleted = file.delete();
                    System.out.println("🧹 빈 폴더 삭제: " + file.getName() + " → " + deleted);
                }
            } else {
                if (Instant.ofEpochMilli(file.lastModified()).isBefore(cutoff)) {
                    boolean deleted = file.delete();
                    System.out.println("🧹 파일 삭제: " + file.getName() + " → " + deleted);
                }
            }
        }
    }
}
