package com.filelist.entity;

import java.io.Serializable;

public class FileDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    private int counter;
    private int jobCount;
    private int newJob;
    private int updateJob;
    private long uploadedOn;
    private long lastModified;
    private long currentTime;
    private long fileSize;
    private String filePath;
    private String serverPath;
    private String taskName;
    private String message;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
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

    public long getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(long uploadedOn) {
        this.uploadedOn = uploadedOn;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((taskName == null) ? 0 : taskName.hashCode());
        result = prime * result + (int) (uploadedOn ^ (uploadedOn >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FileDetail other = (FileDetail) obj;
        if (taskName == null) {
            if (other.taskName != null) {
                return false;
            }
        } else if (!taskName.equals(other.taskName)) {
            return false;
        }
        if (jobCount != other.jobCount) {
            return false;
        }
        if (uploadedOn != other.uploadedOn) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FileDetail [counter=" + counter + ", jobCount=" + jobCount + ", newJob=" + newJob + ", updateJob=" + updateJob + ", uploadedOn=" + uploadedOn + ", lastModified=" + lastModified + ", currentTime=" + currentTime + ", fileSize=" + fileSize
                + ", filePath=" + filePath + ", serverPath=" + serverPath + ", taskName=" + taskName + ", message=" + message + "]";
    }
}
