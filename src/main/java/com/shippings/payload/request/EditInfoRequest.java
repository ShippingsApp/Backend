package com.shippings.payload.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class EditInfoRequest {
    @NotBlank
    private String realName;

    @NotBlank
    private String mobilePhone;
}
