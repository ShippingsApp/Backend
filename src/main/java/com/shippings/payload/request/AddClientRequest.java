package com.shippings.payload.request;

import lombok.Data;

@Data
public class AddClientRequest {

    private String id;
    private String shipId;
    private String start;
    private String finish;
    private String weight;
    private String height;
    private String length;
    private String width;
    private String comment;
    private String price;
}
