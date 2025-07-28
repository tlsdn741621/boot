package com.busanit501.boot_project.controller;

import com.busanit501.boot_project.dto.BoardDTO;
import com.busanit501.boot_project.dto.BoardListAllDTO;
import com.busanit501.boot_project.dto.PageRequestDTO;
import com.busanit501.boot_project.dto.PageResponseDTO;
import com.busanit501.boot_project.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {
    // 업로드 할 , 저장 위치를 불러오기. com.busanit501.upload.path
    @Value("${com.busanit501.upload.path}") // 스프링에서 지원해주는 패키지 경로 사용하기.
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(@AuthenticationPrincipal UserDetails user,PageRequestDTO pageRequestDTO, Model model) {
        // 서비스 외주 이용해서, 데이터 가져오기
        // 1, 기존, 페이징 정보와, 검색 정보만 이용한 리스트 목록,
//        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
        //2. 기존 + 댓글 갯수 포함 목록 정보.
//        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);
        // 3. 1+2 정보 + 첨부 이미지들
        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);
        log.info("BoardController에서, list, responseDTO : {}", responseDTO);
        // 서버 -> 화면으로 데이터 전달.
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("user", user);
    }

    //    등록화면 작업, get
    @PreAuthorize("hasRole('USER')") // 로그인한 유저만 접근 가능.
    @GetMapping("/register") // @AuthenticationPrincipal: 시큐리티에 로그인 된 정보를 관리하는 도구
    public void register(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
    }

    // 등록 처리, post
    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, registerPost 작업중");
        if (bindingResult.hasErrors()) {
            log.info("registerPost, 입력 작업중, 유효성 체크에 해당 사항 있음");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }
        log.info("넘어온 데이터 확인 boardDTO : " + boardDTO);
        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno);
        return "redirect:/board/list";
    }

    // 상세보기 화면, 수정하는 화면 동일.
    // 읽기 전용, 수정이 가능한 input
    @PreAuthorize("isAuthenticated()") // 로그인한 유저만 상세보기 , 수정폼 접근 가능.
    @GetMapping({"/read", "/update"})// 화면 경로 : /board/read.html 작업함.
    // 예시
    //http://localhost:8080/board/list?type=tcw&keyword=1&page=2
    // type, keyword, page, -> PageRequestDTO의 멤버 이름과 동일함.
    // 그래서, 자동 수집함. !!중요!!
    // 자동 화면으로 전달도 함. !!중요!!
    public void read(@AuthenticationPrincipal UserDetails user,Long bno, PageRequestDTO pageRequestDTO,
                     Model model) {
        // 누구에게 외주 줄까요? BoardService  외주,
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("BoardController 에서, read 작업중 boardDTO: " + boardDTO);
        // 서버 -> 화면, 데이터 전달,
        model.addAttribute("dto", boardDTO);
        model.addAttribute("user", user);

    }

    // 수정 처리 post 작업,
    @PostMapping("/update")
    // principal.username : 로그인한 유저,
    // #boardDTO.writer : 게시글 작성자,
    @PreAuthorize("principal.username == #boardDTO.writer")
    public String update(PageRequestDTO pageRequestDTO,
                         @Valid BoardDTO boardDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        //화면에서, 데이터가 잘 전달 되는지 확인.
        log.info("BoardController 에서, update 작업중 boardDTO:" + boardDTO);
        if (bindingResult.hasErrors()) {
            log.info("update, 입력 작업중, 유효성 체크에 해당 사항 있음");
            String link = pageRequestDTO.getLink();
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            // 수정중, 잘못된 오류가 발생시, 현재 수정하는 화면으로 이동하는게 더 좋다.
            // 현재 , 수정하고 있는 게시글의 번호 정보가 필요함.
            // 쿼리 스트링으로 bno 번호는 전달하고,
            redirectAttributes.addAttribute("bno", boardDTO.getBno());
            return "redirect:/board/update?" + link;
        }
        boardService.modify(boardDTO);
        redirectAttributes.addFlashAttribute("result", "수정완료");
        redirectAttributes.addAttribute("bno", boardDTO.getBno());
        return "redirect:/board/read";
    }

    @PostMapping("/remove")
    // 추가, 첨부이미지들도 같이 삭제 진행해야함.
    // 삭제 준비물 1) 첨부 이미지들의 파일 목록 필요함.
    // 서버에서 받으려면, DTO 자동 수집
    @PreAuthorize("principal.username == #boardDTO.writer")
    public String remove(BoardDTO boardDTO, RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, remove 작업중 , 넘어온 bno 확인: " + boardDTO.getBno());
        boardService.remove(boardDTO.getBno());

        //추가, 첨부 이미지들을 삭제 해야함.
        log.info("삭제 작업 , 컨트롤러, 첨부된 파일 목록 : " + boardDTO.getFileNames());
        List<String> fileNames = boardDTO.getFileNames();
        if (fileNames != null && fileNames.size() > 0) {
            // 추가해야함.
            removeFiles(fileNames);
        }

        // 첨부된 댓글이 있다면 댓글도 같이 삭제해야함.


        redirectAttributes.addFlashAttribute("result", "삭제완료!!");
        return "redirect:/board/list";
    }

    public void removeFiles(List<String> files) {
        for (String fileName : files) {
            // 스프링에제공해주는 파일 삭제 메소드 사용해서, 실제 미디어 저장소 삭제 진행.
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();
            try {
                // 마임 타입이 image 이면 확인 하는 용도
                String contentType = Files.probeContentType(resource.getFile().toPath());
                // 원본 삭제
                resource.getFile().delete();

                // 썸네일도 같이 삭제
                if (contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator +
                            "s_" + fileName);
                    thumbnailFile.delete();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

}
