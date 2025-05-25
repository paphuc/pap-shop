package com.pap_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO (Data Transfer Object) for login request.
 * Contains the necessary fields for a login request.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String emailOrPhoneOrUsername;
    private String password;
}
