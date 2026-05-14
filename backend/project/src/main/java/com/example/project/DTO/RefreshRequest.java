package com.example.project.DTO;


// when browser asks for new jwt token ,it asks with this object,it sends refresh token,we check in the db
// if it is there and not exprired we return new token
import lombok.Data;

@Data
public class RefreshRequest
{
    private String refreshToken;
}
