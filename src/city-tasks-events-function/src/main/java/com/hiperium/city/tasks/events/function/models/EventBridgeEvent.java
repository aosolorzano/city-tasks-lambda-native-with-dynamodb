package com.hiperium.city.tasks.events.function.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
public class EventBridgeEvent {

    @NotEmpty
    private String version;

    @NotEmpty
    private String id;

    @NotEmpty
    @ToString.Exclude
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
