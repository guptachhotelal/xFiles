package com.filelist.service;

import com.filelist.entity.FeedProperties;
import com.filelist.entity.FileDetail;
import com.filelist.utils.exception.FileListException;
import com.fl.utils.Constants;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractDirectoryService implements DirectoryService {

    protected static final Logger LOGGER = LogManager.getLogger(DirectoryService.class.getName());
    public static final ConcurrentMap<String, FileDetail> MAP_FILE_DETAILS = new ConcurrentHashMap<>();
    @Resource
    private FeedProperties fileProperties;

    @Override
    public void init() {
        String sDir = "";
        if (Constants.CURRENT_DATE.equals(fileProperties.getDirectory())) {
            Constants.SDF.applyPattern(fileProperties.getDatePattern());
            sDir = Constants.SDF.format(new Date());
        }

        final Path feedHome = Paths.get(fileProperties.getFeedHome(), sDir);
        new Thread(() -> {
            try (WatchService watchService = feedHome.getFileSystem().newWatchService();) {
                DirectoryWatcher directoryWatcher = new DirectoryWatcher(watchService);
                Thread thread = new Thread(directoryWatcher, "directoryWatcher");
                thread.start();
                feedHome.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                thread.join();
            }
            catch (IOException | InterruptedException e) {
                LOGGER.info("Exception occured in AbstractDirectoryService-init " + FileListException.stackTraceToString(e));
            }
        }, "runnerThread").start();
    }

    @Override
    public void removeFromMap(String fileName) {
        LOGGER.info("Removing feed file from map");
        MAP_FILE_DETAILS.remove(fileName);
    }

    @Override
    public List<FileDetail> getFileDetails() {
        LOGGER.info("Returning list of filedetail from map");
        return new CopyOnWriteArrayList<>(MAP_FILE_DETAILS.values());
    }

    @Override
    public Map<String, FileDetail> getFileDetailMap() {
        return MAP_FILE_DETAILS;
    }

    @Override
    public Map<Integer, List<FileDetail>> searchFiles(List<FileDetail> fileDetails, String sText, int pageNumber, int size) {
        LOGGER.info("Loading/Searching/Filtering filedetails");
        Map<Integer, List<FileDetail>> map = new HashMap<>();
        int count = 0;
        int idx = (pageNumber - 1) * size;
        List<FileDetail> pageFiles = new ArrayList<>();
        List<FileDetail> filteredFiles = new ArrayList<>();
        fileDetails.parallelStream().filter((fileDetail) -> (fileDetail.getTaskName().toLowerCase().contains(sText) || fileDetail.getTaskName().matches(sText) || fileDetail.getMessage() != null && fileDetail.getMessage().toLowerCase().startsWith(sText))).forEachOrdered((fileDetail) -> {
            filteredFiles.add(fileDetail);
        });
        for (; idx < filteredFiles.size(); idx++) {
            FileDetail fileDetail = filteredFiles.get(idx);
            ++count;
            if (count <= size) {
                pageFiles.add(fileDetail);
            }
        }
        map.put(filteredFiles.size(), pageFiles);
        return map;
    }

    class DirectoryWatcher implements Runnable {

        private WatchService watchService;

        DirectoryWatcher(WatchService watchService) {
            this.watchService = watchService;
        }

        @Override
        public void run() {
            try {
                WatchKey watchKey = watchService.take();
                while (watchKey != null) {
                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    events.forEach((event) -> {
                        String taskName = event.context().toString();
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            System.out.println("Feed created: " + taskName);
                            addEditMap(taskName, false);
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            System.out.println("Feed Updated: " + taskName);
                            addEditMap(taskName, true);
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            System.out.println("Feed delete: " + taskName);
                            removeFromMap(taskName);
                        } else {
                            System.out.println("Feed overflow: " + taskName);
                            generateFileDetail(true);
                        }
                    });
                    watchKey.reset();
                    watchKey = watchService.take();
                }
            }
            catch (InterruptedException e) {
                LOGGER.info("Exception occured in DirectoryWatcher-run " + FileListException.stackTraceToString(e));
            }
        }
    }

}
