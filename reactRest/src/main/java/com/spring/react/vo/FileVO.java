package com.spring.react.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class FileVO {
    private int file_id;
    private int board_no;
    private String origin_nm;
    private String saved_nm;
    private String file_path;
    private Long file_size;
    private String file_type;
    private LocalDateTime insert_date;
}
