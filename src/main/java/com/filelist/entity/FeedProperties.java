package com.filelist.entity;

import java.io.Serializable;

public final class FeedProperties implements Serializable {

    private static final long serialVersionUID = 1L;
    private String feedDir;
    private String feedExtension;
    private String serverPath;
    private String tagLadder;

    public String getFeedDir() {
        return feedDir;
    }

    public void setFeedDir(String feedDir) {
        this.feedDir = feedDir;
    }

    public String getFeedExtension() {
        return feedExtension;
    }

    public void setFeedExtension(String feedExtension) {
        this.feedExtension = feedExtension;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public String getTagLadder() {
        return tagLadder;
    }

    public void setTagLadder(String tagLadder) {
        this.tagLadder = tagLadder;
    }

    @Override
    public String toString() {
        return "FeedProperties [feedDir=" + feedDir + ", feedExtension=" + feedExtension + ", serverPath=" + serverPath + ", tagLadder=" + tagLadder + "]";
    }
}
