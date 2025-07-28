package com.busanit501.boot_project.service;

import com.busanit501.boot_project.dto.MemberJoinDTO;

public interface MemberService {
    // mid로 디비에 회원 여부 확인
    static class MidExistException extends Exception {

    }

    // 회원가입 로직 처리
    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;
}
