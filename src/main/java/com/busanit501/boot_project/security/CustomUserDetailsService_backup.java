package com.busanit501.boot_project.security;//package com.busanit501.boot_project.security;
//
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//// 시큐리티에서는 로그인시, 원하는 포맷 형태가 있음.
//// 시큐리티가 만들어 둔 UserDetails 라는 타입으로 맞춰주기.
//// 시큐리티에서 제공하는 UserDetailsService 라는 인터페이스 구현하면
//// 좀 더 쉽게 , 시큐리티가 원하는 타입을 만들수 있음.
//@Log4j2
//@Service
////@RequiredArgsConstructor
//public class CustomUserDetailsService_backup implements UserDetailsService {
//
//    @Autowired
//    private  PasswordEncoder passwordEncoder;
//
//    // 업무
//    // 시큐리티에 폼방식으로 접근하는 유저명을 확인하고, 디비에 저장된 유저가 맞다면,
//    // 접근 할수 있는 타입을 전달해줌.
//    // 화면에서 전달받은 인증 , 아이디 와
//    // 디비에 있는 아이디의 일치 여부를 검사하는 직원.
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        log.info("====loadUserByUsername : 전달받은 유저명(아이디) 확인 ===================================");
//        log.info("username: " + username);
//
//        // 작업2
//        // 더미 데이터 작업, 유저 : user1 , 패스워드 : 1111(평문) , 암호화 해보기.
//        UserDetails userDetails = User.builder().username("user1")
//                .password(passwordEncoder.encode("1111"))
//                .authorities("ROLE_USER")
//                .build();
//
//        return userDetails;
//    }
//}
