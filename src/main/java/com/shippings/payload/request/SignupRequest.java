package com.shippings.payload.request;

import lombok.Data;
import java.util.Set;
import javax.validation.constraints.*;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 10)
    private String chosenRole;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String mobilePhone;
    private String realName;

}
