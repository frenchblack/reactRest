package com.spring.react.vo.security;

import java.io.Serializable;

public class JwtResponse implements Serializable {
	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
	private final String jwtrefreshtoekn;

	public JwtResponse(String jwttoken, String jwtrefreshtoekn) {
		this.jwttoken = jwttoken;
		this.jwtrefreshtoekn = jwtrefreshtoekn;
	}

	public String getToken() {
		return this.jwttoken;
	}
	
	public String getRefreshtoken() {
		return this.jwtrefreshtoekn;
	}
}
