package com.busanit501.boot_project.controller;

import com.busanit501.boot_project.dto.MemberJoinDTO;
import com.busanit501.boot_project.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/login")
    public void loginGet(String error, String logout) {
        log.info("====커스텀 로그인 페이지 화면 get===============================");
        log.info("logout : " + logout);

        if(logout != null) {
            log.info("유저 , 로그아웃 진행 함. =============");
        }
    }

    // /member/join
    @GetMapping("/join")
    public void joinGet() {

    }

    @PostMapping("/join")
    public String joinPost(MemberJoinDTO memberJoinDTO, RedirectAttributes redirectAttributes) {
        log.info("멤버 컨트롤러에서, 조인 로직 처리 작업 중. ");
        log.info("화면에서 전달받은 회원 가입 유저 데이터 확인 : " + memberJoinDTO);

        // 멤버서비스를 이용한 로직 처리가 필요함.
        try {
            memberService.join(memberJoinDTO);
        }catch (MemberService.MidExistException e){
            redirectAttributes.addFlashAttribute("error","mid");
            return "redirect:/member/join";
        }
        redirectAttributes.addFlashAttribute("success","회원가입 성공");

        return "redirect:/member/login";
    }

}
