package com.busanit501.boot_project.service;

import com.busanit501.boot_project.dto.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister() {
        // 입력할 더미 데이터 , 준비물 준비하고,
        BoardDTO boardDTO = BoardDTO.builder()
                .title("서비스 작업 단위 테스트 중")
                .content("서비스 작업 단위 테스트 내용 작성중 ")
                .writer("이상용")
                .build();
        // 실제 서비스 이용해서, 외주 주기.
        boardService.register(boardDTO);
    }

    @Test
    public void testReadOne() {
        // 실제 디비 번호 bno, 각자 디비에 있는 내용으로 조회하기.
        BoardDTO boardDTO = boardService.readOne(102L);
        log.info("서비스 단위테스트에서 하나 조회 boardDTO : " + boardDTO);
    }

    @Test
    public void testModify() {
        // 수정할 실제 데이터 이용, 201L
        BoardDTO boardDTO = boardService.readOne(201L);
        boardDTO.setTitle("수정2 테스트 ");
        boardDTO.setContent("오늘 점심 뭐 먹지 ??");

        // 첨부 파일 추가.
        boardDTO.setFileNames(Arrays.asList(UUID.randomUUID()+"_apple.jpg"));

        boardService.modify(boardDTO);
    }

    @Test
    public void testDelete() {
        boardService.remove(102L);
    }

    @Test
    public void testList() {
        // 화면으로부터 전달 받은 , 페이징 정보, 검색 정보, 더미 데이터 만들기.
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
        log.info("서비스 테스트 작업 중, responseDTO : " + responseDTO);
    }

    @Test
    public void testSearchWithReplyCount() {
        // 화면으로부터 전달 받은 , 페이징 정보, 검색 정보, 더미 데이터 만들기.
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("ㅇ")
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);
        log.info("서비스 테스트 작업 중2, responseDTO : " + responseDTO);

    }

    // 게시글 + 첨부 이미지
    @Test
    public void testRegisterWithImages() {
        log.info("서비스 단위테스트에서, 이미지 포함 게시글 작성 테스트 중. ");
        // 더미 데이터 준비물 준비 작업
        BoardDTO boardDTO = BoardDTO.builder()
                .title("오늘 점심 뭐 먹지? 이미지 첨부용")
                .content("도시락? 라면? 뭐 먹지??")
                .writer("이상용")
                .build();
        //더미 첨부 이미지 추가
        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aaa.jpg",
                        UUID.randomUUID()+"_bbb.jpg",
                        UUID.randomUUID()+"_ccc.jpg"
                )
        );

        // 실제 디비에 반영하기.
        Long bno = boardService.register(boardDTO);
        log.info("등록된 게시글 번호 확인: " + bno);
    }

    @Test
    public void testReadAll() {
        // 조회할 실제 디비 확인.
        Long bno = 297L;
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("서비스 단윈 테스트에서, testReadAll, boardDTO : " + boardDTO);
        // 첨부된 이미지 들도 확인
        for(String fileName : boardDTO.getFileNames()){
            log.info("첨부된 이미지 확인 : " + fileName);
        }
    }

    @Test
    public void testRemoveAll() {
        Long bno = 201L;
        boardService.remove(bno);
    }

    @Test
    public void testListWithAll() {
        // 더미 데이터 준비물 작업.
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();
        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);
        List<BoardListAllDTO> dtoList = responseDTO.getDtoList();
        dtoList.forEach(dto -> {
            log.info("dto : " + dto);
            log.info("dto 의 제목 : " + dto.getTitle());

            // 첨부 이미지들 조회
            if(dto.getBoardImages() != null && dto.getBoardImages().size() > 0){
                for (BoardImageDTO boardImageDTO : dto.getBoardImages()) {
                    log.info("boardImageDTO : " + boardImageDTO);
                }
            }
        });
    }


}
