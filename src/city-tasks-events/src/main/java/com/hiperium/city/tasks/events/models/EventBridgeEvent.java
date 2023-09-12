package com.hiperium.city.tasks.events.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EventBridgeEvent {

    @NotEmpty
    private String version;

    @NotEmpty
    private String id;

    @NotEmpty
    private String account;

    @NotEmpty
    private String source;

    @NotEmpty
    private String region;

    private Date time;

    private List<String> resources;

    @JsonProperty("detail-type")
    private String detailType;

    @NotNull
    private TaskEventDetail detail;
}
