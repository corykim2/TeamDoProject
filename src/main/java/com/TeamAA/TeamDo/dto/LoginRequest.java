package com.TeamAA.TeamDo.dto;


public class LoginRequest {

    private String id;
    private String password;

    // 기본 생성자 (JSON 파싱을 위해 필수)
    public LoginRequest() {}

    // 전체 생성자
    public LoginRequest(String id, String password) {
        this.id = id;
        this.password = password;
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
