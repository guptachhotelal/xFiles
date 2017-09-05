package com.filelist.utils;

import com.filelist.entity.FeedProperties;
import java.io.File;
import java.io.FilenameFilter;
import javax.annotation.Resource;

public class FileTypeFilter implements FilenameFilter {

    @Resource
    private FeedProperties fileListProperties;

    @Override
    public boolean accept(File dir, String name) {
        if (name.lastIndexOf('.') > 0) {
            int lastIndex = name.lastIndexOf('.');
            String str = name.substring(lastIndex + 1);
            if (str.equals(fileListProperties.getFeedExtension())) {
                return true;
            }
        }
        return false;
    }

}
