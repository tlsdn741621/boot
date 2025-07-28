package com.busanit501.boot_project.repository;

import com.busanit501.boot_project.domain.Member;
import com.busanit501.boot_project.domain.MemberRole;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MemberRepositoryTests {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertMembers() {
        IntStream.rangeClosed(1,100).forEach(i ->{
            Member member = Member.builder()
                    .mid("member"+i)
                    .mpw(passwordEncoder.encode("1111"))
                    .email("email"+i+"@test.com")
                    .build();

            // 권한 추가.
            member.addRole(MemberRole.USER);
            if (i >=95) {
                member.addRole(MemberRole.ADMIN);
            }
            memberRepository.save(member);
        });
    }

    @Test
    public void testRead() {
    Optional<Member> result = memberRepository.getWithRoles("member100");
    Member member = result.orElseThrow();
    log.info(member);
    log.info(member.getRoleSet());

    member.getRoleSet().forEach(role -> log.info(role.name()));
    }

}
