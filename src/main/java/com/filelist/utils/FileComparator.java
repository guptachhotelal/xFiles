package com.filelist.utils;

import com.filelist.entity.FileDetail;
import java.util.Comparator;

public class FileComparator implements Comparator<FileDetail> {

    private int column;
    private boolean asc;

    public FileComparator(int column, boolean asc) {
        this.column = column;
        this.asc = asc;
    }

    @Override
    public int compare(FileDetail fd1, FileDetail fd2) {
        int val = 0;
        switch (column) {
            case 0:
                if (asc) {
                    if (fd1.getCounter() > fd2.getCounter()) {
                        val = 1;
                    } else if (fd1.getCounter() < fd2.getCounter()) {
                        val = -1;
                    }
                    return val;
                } else {
                    if (fd1.getCounter() > fd2.getCounter()) {
                        val = -1;
                    } else if (fd1.getCounter() < fd2.getCounter()) {
                        val = 1;
                    }
                    return val;
                }
            case 1:
                if (asc) {
                    return fd1.getTaskName().compareToIgnoreCase(fd2.getTaskName());
                } else {
                    return fd2.getTaskName().compareToIgnoreCase(fd1.getTaskName());
                }
            case 2:
                if (asc) {
                    if (fd1.getJobCount() > fd2.getJobCount()) {
                        val = 1;
                    } else if (fd1.getJobCount() < fd2.getJobCount()) {
                        val = -1;
                    }
                    return val;
                } else {
                    if (fd1.getJobCount() > fd2.getJobCount()) {
                        val = -1;
                    } else if (fd1.getJobCount() < fd2.getJobCount()) {
                        val = 1;
                    }
                    return val;
                }
            case 3:
                if (asc) {
                    if (fd1.getUploadedOn() > fd2.getUploadedOn()) {
                        val = 1;
                    } else if (fd1.getUploadedOn() < fd2.getUploadedOn()) {
                        val = -1;
                    }
                    return val;
                } else {
                    if (fd1.getUploadedOn() > fd2.getUploadedOn()) {
                        val = -1;
                    } else if (fd1.getUploadedOn() < fd2.getUploadedOn()) {
                        val = 1;
                    }
                    return val;
                }
            case 4:
                if (asc) {
                    if (fd1.getLastModified() > fd2.getLastModified()) {
                        val = 1;
                    } else if (fd1.getLastModified() < fd2.getLastModified()) {
                        val = -1;
                    }
                    return val;
                } else {
                    if (fd1.getLastModified() > fd2.getLastModified()) {
                        val = -1;
                    } else if (fd1.getLastModified() < fd2.getLastModified()) {
                        val = 1;
                    }
                    return val;
                }
            case 5:
                if (asc) {
                    if (fd1.getFileSize() > fd2.getFileSize()) {
                        val = 1;
                    } else if (fd1.getFileSize() < fd2.getFileSize()) {
                        val = -1;
                    }
                    return val;
                } else {
                    if (fd1.getFileSize() > fd2.getFileSize()) {
                        val = -1;
                    } else if (fd1.getFileSize() < fd2.getFileSize()) {
                        val = 1;
                    }
                    return val;
                }
            default:
                if (asc) {
                    return fd1.getTaskName().compareToIgnoreCase(fd2.getTaskName());
                } else {
                    return fd2.getTaskName().compareToIgnoreCase(fd1.getTaskName());
                }
        }
    }
}
