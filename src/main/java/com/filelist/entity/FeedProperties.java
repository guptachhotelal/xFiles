package com.filelist.entity;

import java.io.Serializable;

public final class FeedProperties implements Serializable {

    private static final long serialVersionUID = 1L;
    private String feedHome;
    private String directory;
    private String datePattern;
    private String feedExtension;
    private String serverHome;
    private String tagLadder;

    public FeedProperties(String feedHome, String directory) {
        this.feedHome = feedHome;
        this.directory = directory;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getFeedHome() {
        return feedHome;
    }

    public String getDirectory() {
        return directory;
    }

    public String getFeedExtension() {
        return feedExtension;
    }

    public void setFeedExtension(String feedExtension) {
        this.feedExtension = feedExtension;
    }

    public String getServerHome() {
        return serverHome;
    }

    public void setServerHome(String serverHome) {
        this.serverHome = serverHome;
    }

    public String getTagLadder() {
        return tagLadder;
    }

    public void setTagLadder(String tagLadder) {
        this.tagLadder = tagLadder;
    }

    @Override
    public String toString() {
        return "FeedProperties [feedHome=" + feedHome + ", directory=" + directory + ", datePattern=" + datePattern + ", feedExtension=" + feedExtension + ", serverHome=" + serverHome + ", tagLadder=" + tagLadder + "]";
    }
}
