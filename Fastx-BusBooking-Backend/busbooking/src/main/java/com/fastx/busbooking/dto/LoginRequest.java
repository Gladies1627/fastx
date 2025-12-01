package com.fastx.busbooking.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String name;   
    private String password;
}
