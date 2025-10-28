package com.g5.entity.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RegisterDTO {
    private String username;
    private String password;
    private String realName;
    private String role;
}
