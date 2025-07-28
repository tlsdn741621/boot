package com.busanit501.boot_project.security;

import com.busanit501.boot_project.domain.Member;
import com.busanit501.boot_project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;
// 시큐리티에서는 로그인시, 원하는 포맷 형태가 있음.
// 시큐리티가 만들어 둔 UserDetails 라는 타입으로 맞춰주기.
// 시큐리티에서 제공하는 UserDetailsService 라는 인터페이스 구현하면
// 좀 더 쉽게 , 시큐리티가 원하는 타입을 만들수 있음.

// 업무
// 시큐리티에 폼방식으로 접근하는 유저명을 확인하고, 디비에 저장된 유저가 맞다면,
// 접근 할수 있는 타입을 전달해줌.
// 화면에서 전달받은 인증 , 아이디 와
// 디비에 있는 아이디의 일치 여부를 검사하는 직원.
@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

//    private final MemberRepository memberRepository;

    //시스템에 등록된, 암호화 해주는 도구 가져오기.
//    private PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;


    // 시큐리티에서, 로그인 작업 처리시, 동작하는 메서드 여기
    // 시큐리티에서, 타입을 UserDetails로 반환해야 확인 가능.
    // 결론, 로그인 했을 경우, 입력한 , username, password 값을
    // 여기 메서드로 가지고 온다. 참고, 가지고 오는 키는 고정,
    // 예시) username, password , 키가 고정, 주의사항,
    // 화면에 input 태그에서, name 이름 작성시 주의하기.

//    public CustomUserDetailsService(MemberRepository memberRepository) {
//public CustomUserDetailsService() {
//        this.memberRepository = memberRepository;
//        this.passwordEncoder = new BCryptPasswordEncoder();
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("로그인한 유저 확인 : "+ username);
        // 데이터베이스에 저장된 유저와 비교 작업 후, 처리 예정.
        // 테스트, 더미 데이터 작업,
        // 반환 타입 : UserDetails
        // User : 스프링 시큐리티에서 제공하는 클래스, 주의하기, 이름. 고정.

        // 연습용, 더미 데이터 ,
//        log.info("passwordEncoder.encode(\"123456\") : " +passwordEncoder.encode("123456"));
//        UserDetails userDetails = User.builder()
//                .username("lsy")
//                // 서버에서는 평문으로 패스워드 넘어오면,
//                // 기본 다 거부함. 기본 해쉬한 값으로 와야함.
////                .password("1234")
//                .password(passwordEncoder.encode("1234"))
//                // 인증된 유저,
//                // 관리자, ROLE_ADMIN
//                .authorities("ROLE_USER")
////                .authorities("ROLE_USER", "ROLE_ADMIN")
//                .build();

        // 실제 데이터베이스에서, 디비 조회 후, 로그인 처리 확인.
        Optional<Member> result = memberRepository.getWithRoles(username);
        if(result.isEmpty()){
            throw new UsernameNotFoundException("해당 유저가 없습니다. ");
        }
        Member member = result.get();
        log.info("확인2 loadUserByUsername에서 화면으로부터 입력받은 로그인 정보로 ,디비 조회 확인. member : " + member);



        // MemberSecurityDTO ,사실은 반환 타입 , UserDetails 타입이다,
        // 왜? User 클래스를 상속을 받아서 ,
        MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                member.getMid(),
                member.getMpw(),
                member.getEmail(),
                member.isDel(),
                false,
                // 디비에 저장된 USER, ADMIN
                // -> 시큐리티에서 원하는 구조 인 ROLE_USER, ROLE_ADMIN 형태로 변경하기.
                member.getRoleSet().stream().map(memberRole ->
                        new SimpleGrantedAuthority("ROLE_"+memberRole.name())).collect(Collectors.toList())
        );
        log.info("확인 3 loadUserByUsername에서 화면으로부터 입력받은 로그인 정보로 ,디비 조회 확인2. memberSecurityDTO : " + memberSecurityDTO);

        return memberSecurityDTO;
    }
}