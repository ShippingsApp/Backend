package com.shippings.payload.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AddRoutRequest {

    private String id;
    @NotBlank
    @Size(max = 10)
    private String dateStart;

    @NotBlank
    @Size(max = 10)
    private String dateFinish;

    private String start;
    private String finish;
    private String weight;
    private String height;
    private String length;
    private String width;
    private String plusTime;
    private String comment;
}
