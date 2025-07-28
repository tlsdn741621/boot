package com.busanit501.boot_project.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
@ToString
// 시큐리티에서는 원하는 템플릿 구조,
// 원하는 타입 : UserDetails ,
// 간단한 방법은 시큐리티에서 제공하는 User 클래스를 상속 받으면됨.

public class MemberSecurityDTO extends User {

    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;


    public MemberSecurityDTO(String username, String password,
                             String email, boolean del, boolean social,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.mid = username;
        this.mpw = password;
        this.email = email;
        this.del = del;
        this.social = social;

    }
}
