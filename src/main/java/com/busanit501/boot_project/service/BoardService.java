package com.busanit501.boot_project.service;

import com.busanit501.boot_project.domain.Board;
import com.busanit501.boot_project.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface BoardService {

    Long register(BoardDTO boardDTO);

    BoardDTO readOne(Long bno);

    void modify(BoardDTO boardDTO);

    void remove(Long bno);

    //    기존 , 1) 페이징 2) 검색
    PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);

    // 기존 , 1) 페이징 2) 검색 3) 댓글 갯수 , 버전으로 목록 출력.
    PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);

    // 기존 , 1) 페이징 2) 검색 3) 댓글 갯수 , 버전으로 목록 출력. 4) 첨부 이미지들
    PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    // 엔티티 <-> DTO 변환, 기본 메서드로 정의 해두기.
    //1) dto -> board 변환 하기.
    default Board dtoToEntity(BoardDTO boardDTO) {
        Board board = Board.builder()
                .bno(boardDTO.getBno())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .writer(boardDTO.getWriter())
                .build();

        // 이미지 첨부 작업.
        if(boardDTO.getFileNames() != null) {
            boardDTO.getFileNames().forEach(fileName -> {
                // 기존 파일명에 혹시나, "_" 같이 있는 이름은 오류가 나는게 흠.
                // 썸네일 : s_uuid_이미지파일명.jpg
                // 파일 : uuid_파일명.확장자
                String[] arr= fileName.split("_");
                board.addImage(arr[0], arr[1]);

            }); // end forEach
        } // end if
        return board;
    }

    // 2) board -> dto 변환하기.
    default BoardDTO entityToDTO (Board board) {
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .build();

        // 첨부 파일명 변환 하기. 엔티티 -> 실제 파일명으로 변환 작업중.
        List<String> fileNames = board.getImageSet().stream().sorted().map(boardImage ->
                boardImage.getUuid() + "_"+boardImage.getFileName()).collect(Collectors.toList());

        // 첨부 이미지 원본 파일명을, 다시 dto 담기
        boardDTO.setFileNames(fileNames);
        return boardDTO;

    }



}
