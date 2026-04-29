package com.example.smartvillageapp;
public class JobModel {
    public String jobId, title, requirement, description, postedBy, postedByName, status;

    public JobModel() {}

    public JobModel(String jobId, String title, String requirement,
                    String description, String postedBy, String status) {
        this.jobId = jobId;
        this.title = title;
        this.requirement = requirement;
        this.description = description;
        this.postedBy = postedBy;
        this.status = status;
    }
}
