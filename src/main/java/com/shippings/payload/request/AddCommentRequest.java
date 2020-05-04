package com.shippings.payload.request;

import lombok.Data;

@Data
public class AddCommentRequest {

    private String id;
    private String rate;
    private String comment;

}
