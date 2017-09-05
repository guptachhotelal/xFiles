package com.filelist.entity;

import java.io.Serializable;

public class StatusDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    private String clientName;
    private int taskCount;
    private int jobCount;
    private int newJob;
    private int updateJob;
    private int unchangeJob;
    private int doneCount;
    private int pendingCount;
    private int zeroCount;
    private int errorCount;
    private int todayTaskCount;
    private int todayJobCount;

    public StatusDetail(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getJobCount() {
        return jobCount;
    }

    public void setJobCount(int jobCount) {
        this.jobCount = jobCount;
    }

    public int getNewJob() {
        return newJob;
    }

    public void setNewJob(int newJob) {
        this.newJob = newJob;
    }

    public int getUpdateJob() {
        return updateJob;
    }

    public void setUpdateJob(int updateJob) {
        this.updateJob = updateJob;
    }

    public int getUnchangeJob() {
        return unchangeJob;
    }

    public void setUnchangeJob(int unchangeJob) {
        this.unchangeJob = unchangeJob;
    }

    public int getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(int doneCount) {
        this.doneCount = doneCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    public int getZeroCount() {
        return zeroCount;
    }

    public void setZeroCount(int zeroCount) {
        this.zeroCount = zeroCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getTodayTaskCount() {
        return todayTaskCount;
    }

    public void setTodayTaskCount(int todayTaskCount) {
        this.todayTaskCount = todayTaskCount;
    }

    public int getTodayJobCount() {
        return todayJobCount;
    }

    public void setTodayJobCount(int todayJobCount) {
        this.todayJobCount = todayJobCount;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return "StatusDetail [clientName=" + clientName + ", taskCount=" + taskCount + ", jobCount=" + jobCount + ", doneCount=" + doneCount + ", pendingCount=" + pendingCount + ", zeroCount=" + zeroCount + ", errorCount=" + errorCount
                + ", todayTaskCount=" + todayTaskCount + ", todayJobCount=" + todayJobCount + "]";
    }
}
