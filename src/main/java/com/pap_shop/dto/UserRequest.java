package com.pap_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Integer roleId;
}
