package com.zhumengmiao.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private String username;
    private String title;
    private String content;
}
