package com.hiperium.city.tasks.events.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public TaskEventDetail getDetail() {
        return detail;
    }

    public void setDetail(TaskEventDetail detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "EventBridgeCustomEvent{" +
                "version='" + version + '\'' +
                ", id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", source='" + source + '\'' +
                ", time=" + time +
                ", region='" + region + '\'' +
                ", resources=" + resources +
                ", detailType='" + detailType + '\'' +
                ", detail=" + detail +
                '}';
    }
}
