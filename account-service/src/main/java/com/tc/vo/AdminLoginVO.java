package com.tc.vo;

import lombok.Data;

@Data
public class AdminLoginVO {
    private Integer adminId;
    private String username;
    private String password;
    private String imgUrl;
    private String name;
    private String token;
}
