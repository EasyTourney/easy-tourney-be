package com.example.easytourneybe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseObject {
    private Boolean success;
    private Integer total;
    private Object data;
    //private String message;
    private Map<String, Object> additionalData = new HashMap<>();
    private Map<String, String> errorMessage = new HashMap<>();

    public ResponseObject(Boolean success, Integer total, Object data) {
        this.success = success;
        this.total = total;
        this.data = data;
       //this.message = message;
    }
}
