package com.hiperium.city.tasks.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EventBridgeCustomEvent {

    @JsonProperty("version")
    private String version;

    @JsonProperty("id")
    private String id;

    @JsonProperty("account")
    private String account;

    @JsonProperty("source")
    private String source;

    @JsonProperty("time")
    private Date time;

    @JsonProperty("region")
    private String region;

    @JsonProperty("resources")
    private List<String> resources;

    @JsonProperty("detail-type")
    private String detailType;

    @JsonProperty("detail")
    private TaskEventDetail detail;
}
