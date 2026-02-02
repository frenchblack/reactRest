package com.spring.react.vo.users;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVO implements UserDetails{
	private String user_id;
	private String user_pw;
	private String user_nm;
	private String mail;
	private String role_cd;
	
	// 사용자의 권한을 콜렉션 형태로 반환
    // 단, 클래스 자료형은 GrantedAuthority를 구현해야함
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    String role = (role_cd == null || role_cd.equals(""))
	            ? "ROLE_USER"
	            : role_cd;

	    return java.util.List.of(
	        new org.springframework.security.core.authority.SimpleGrantedAuthority(role)
	    );
	}
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user_pw;
	}
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user_id;
	}
	
	// 계정 만료 여부 반환
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	
	// 계정 잠금 여부 반환
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}
	
	// 패스워드의 만료 여부 반환
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	
	// 계정 사용 가능 여부 반환
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public String toString() {
		return "UserVO [user_id=" + user_id + ", user_pw=" + user_pw + ", user_nm=" + user_nm + ", mail=" + mail + "]";
	}

}
