package com.spring.react.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.spring.react.vo.BoardVO;
import com.spring.react.vo.FileVO;

@Repository
@Mapper
public interface BoardMapper {
	List<BoardVO> getBoardList(int offset, int size, String keyword, String type, String category, String subCategory, String menu_cd, String sort, String period);

	int postBoard(BoardVO boardVo);

	BoardVO viewBoard(int board_no);

	int increaseViewCnt(int board_no);

	int updateContent(int board_no, String content);

	int saveFileMuti(@Param("fileList") List<FileVO> fileVo);

	List<FileVO> getFileList(int board_no);
}
