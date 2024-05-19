package com.example.WeeSeed.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private String id;
    private String password;

    private String email;

    private String state;
    @Builder
    UserDto(String id, String password,String email,String state){
        this.id = id;
        this.password = password;
        this.email = email;
        this.state = state;
    }
}