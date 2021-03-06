package com.kwery.dtos;

import com.kwery.models.JobModel;

/**
 * If we serialize JobModel using Jackson, parentJob field causes error due to Jackson's self reference shortcoming. This is a hack to overcome this issue.
 */
public class JobModelHackDto {
    private JobModel jobModel;
    private JobModel parentJobModel;
    private String lastExecution = "";
    private String nextExecution = "";

    public JobModelHackDto(JobModel jobModel) {
        this.jobModel = jobModel;
    }

    public JobModelHackDto(JobModel jobModel, JobModel parentJobModel) {
        this.jobModel = jobModel;
        this.parentJobModel = parentJobModel;
    }

    //For JSON path deserialisation in test cases
    private JobModelHackDto() {
    }

    public JobModel getJobModel() {
        return jobModel;
    }

    public void setJobModel(JobModel jobModel) {
        this.jobModel = jobModel;
    }

    public JobModel getParentJobModel() {
        return parentJobModel;
    }

    public void setParentJobModel(JobModel parentJobModel) {
        this.parentJobModel = parentJobModel;
    }

    public String getLastExecution() {
        return lastExecution;
    }

    public void setLastExecution(String lastExecution) {
        this.lastExecution = lastExecution;
    }

    public String getNextExecution() {
        return nextExecution;
    }

    public void setNextExecution(String nextExecution) {
        this.nextExecution = nextExecution;
    }
}
