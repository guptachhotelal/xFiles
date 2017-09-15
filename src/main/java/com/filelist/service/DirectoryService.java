package com.filelist.service;

import com.filelist.entity.FileDetail;
import java.util.List;
import java.util.Map;

public interface DirectoryService {

    String NEW_UPDATED_JOBS = "new_updated";
    String NEW_JOBS_ONLY = "only_new";
    String UPDATED_JOBS_ONLY = "only_updated";
    String UNCHANGED_JOBS_ONLY = "only_unchanged";

    void init();

    Map<Integer, List<FileDetail>> searchFiles(List<FileDetail> fileDetails, String searchText, int pageNumber, int length);

    void removeFromMap(String fileName);

    List<FileDetail> getFileDetails();

    Map<String, FileDetail> getFileDetailMap();

    void addEditMap(String fileName, boolean update);

    void generateFileDetail(boolean clean);

}
