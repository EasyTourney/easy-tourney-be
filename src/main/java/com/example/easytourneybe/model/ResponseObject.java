package com.example.easytourneybe.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class ResponseObject {
    private Boolean success;
    private Integer total;
    private Object data;
    private String message;
    private Map<String, Object> additionalData = new HashMap<>();

    public ResponseObject(Boolean success, Integer total, Object data, String message) {
        this.success = success;
        this.total = total;
        this.data = data;
        this.message = message;
    }
}
