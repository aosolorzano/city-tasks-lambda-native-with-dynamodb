package com.hiperium.city.tasks.events.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventBridgeResponse {

    private int statusCode;
    private Map<String, String> headers;
    private String body;
}
