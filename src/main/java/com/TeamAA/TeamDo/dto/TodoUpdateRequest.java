package com.TeamAA.TeamDo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;


public class TodoUpdateRequest {
    private String name;
    private LocalDate deadline;
    private String state;
    private Integer priority;

    public LocalDate getDeadline() {return deadline;}

    public String getName() {return name;}

    @Min(1)
    @Max(5)
    public  Integer getPriority() { return priority;}

    public String getState() { return state;}

    public void setName(String name) {
        this.name = name;
    }
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

}
