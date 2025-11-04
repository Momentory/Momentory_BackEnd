package com.example.momentory.global.security;

import com.example.momentory.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long userId; // 프로젝트에서 사용할 사용자 ID
    private final String email;
    private final Collection<? extends SimpleGrantedAuthority> authorities;

    // 생성자 (필요한 정보만 받도록 간소화)
    public UserPrincipal(Long userId, String email, Collection<? extends SimpleGrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.authorities = authorities;
    }

    // --- UserDetails 인터페이스 구현 메서드 (주요 로직) ---

    @Override
    public Collection<? extends SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // 비밀번호는 이 객체에 저장하지 않음
    }

    @Override
    public String getUsername() {
        return email; // Username으로 Email을 사용한다고 가정
    }

    // 계정 상태 관련 설정 (모두 true로 설정하여 사용 가능하도록 함)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}