package com.filelist.service;

import com.filelist.entity.FeedProperties;
import com.filelist.entity.FileDetail;
import com.filelist.entity.TimeZoneOffset;
import com.filelist.utils.FileConnection;
import com.filelist.utils.exception.FileListException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import org.apache.commons.lang3.StringEscapeUtils;

public class XMLDirectoryService extends AbstractDirectoryService {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    @Resource
    private TimeZoneOffset timeZoneOffset;
    @Resource
    private FilenameFilter xmlFileFilter;
    @Resource
    private FileConnection fileConnection;
    @Resource
    private FeedProperties fileProperties;

    @Override
    public void addEditMap(String fileName, boolean update) {
        Date today = new Date();
        LOGGER.info("Adding/Updating feed files");
        LOGGER.info("Setting added/updated file details");
        Calendar calendar = Calendar.getInstance();
        int cHour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date tDay = calendar.getTime();

        calendar.add(Calendar.DATE, timeZoneOffset.getDayOffset());
        Date pDay = calendar.getTime();

        long cTime = cHour >= timeZoneOffset.getTimeOffset() ? tDay.getTime() : pDay.getTime();

        String filePath = fileProperties.getFeedDir() + File.separator + fileName;
        FileDetail fileDetail = new FileDetail();

        if (update) {
            fileDetail.setCounter(MAP_FILE_DETAILS.get(fileName).getCounter());
            LOGGER.info("Updating task file " + fileName);
        } else {
            fileDetail.setCounter(MAP_FILE_DETAILS.size() + 1);
            LOGGER.info("Adding new task file " + fileName);
        }

        fileDetail.setFilePath(filePath);
        fileDetail.setTaskName(fileName.substring(0, fileName.lastIndexOf(fileProperties.getFeedExtension()) - 1));
        fileDetail.setServerPath(fileProperties.getServerPath() + fileName);
        try {
            URI fileURI = new File(StringEscapeUtils.escapeXml11(filePath)).toURI();
            String countQuery = "count(doc(\"" + fileURI + "\")/" + fileProperties.getTagLadder() + ")";
            fileDetail.setJobCount(count(countQuery));
            String newQuery = "count(doc(\"" + fileURI + "\")/" + fileProperties.getTagLadder() + "[contains(jobdatum,'" + SDF.format(today) + "')])";
            int newCount = count(newQuery);
            fileDetail.setNewJob(newCount);
            String updateQuery = "count(doc(\"" + fileURI + "\")/" + fileProperties.getTagLadder() + "[contains(date_of_last_change,'" + SDF.format(today) + "')])";
            int updateCount = count(updateQuery);
            fileDetail.setUpdateJob(updateCount);
            if (newCount > 0 && updateCount > 0) {
                fileDetail.setMessage(NEW_UPDATED_JOBS);
            } else if (newCount > 0 && updateCount == 0) {
                fileDetail.setMessage(NEW_JOBS_ONLY);
            } else if (newCount == 0 && updateCount > 0) {
                fileDetail.setMessage(UPDATED_JOBS_ONLY);
            } else {
                fileDetail.setMessage(UNCHANGED_JOBS_ONLY);
            }
            File file = new File(filePath);
            fileDetail.setFileSize(file.length());
            Path path = Paths.get(file.getAbsolutePath());
            BasicFileAttributes view = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
            long uploadedOn = view.creationTime().to(TimeUnit.MILLISECONDS);
            fileDetail.setUploadedOn(uploadedOn);
            fileDetail.setLastModified(file.lastModified());
            fileDetail.setCurrentTime(cTime);
            LOGGER.info("Added/Updated task " + fileDetail);
            MAP_FILE_DETAILS.put(fileName, fileDetail);
        } catch (IOException | XQException e) {
            LOGGER.info("Exception occured in method XMLDirectoryService-addEditMap " + FileListException.stackTraceToString(e));
            fileDetail.setJobCount(-1);
            fileDetail.setMessage("Error in task " + fileDetail.getTaskName());
        }
    }

    @Override
    public void generateFileDetail(boolean clean) {
        Date today = new Date();
        if (clean) {
            LOGGER.info("Clearing map");
            MAP_FILE_DETAILS.clear();
        }
        Calendar calendar = Calendar.getInstance();
        int cHour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date tDay = calendar.getTime();

        calendar.add(Calendar.DATE, timeZoneOffset.getDayOffset());
        Date pDay = calendar.getTime();

        long cTime = cHour >= timeZoneOffset.getTimeOffset() ? tDay.getTime() : pDay.getTime();

        File[] files = new File(fileProperties.getFeedDir()).listFiles(xmlFileFilter);
        if (files == null) {
            return;
        }
        LOGGER.info("Adding file details in map");
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.exists()) {
                continue;
            }
            FileDetail fileDetail = new FileDetail();
            fileDetail.setCounter(i + 1);
            fileDetail.setFilePath(file.getAbsolutePath());
            String fileName = file.getName();

            fileDetail.setTaskName(fileName.substring(0, fileName.lastIndexOf(fileProperties.getFeedExtension()) - 1));
            fileDetail.setServerPath(fileProperties.getServerPath() + fileName);
            try {
                URI fileURI = new File(StringEscapeUtils.escapeXml11(file.getAbsolutePath())).toURI();
                String countQuery = "count(doc(\"" + fileURI + "\")/" + fileProperties.getTagLadder() + ")";
                fileDetail.setJobCount(count(countQuery));
                String newQuery = "count(doc(\"" + fileURI + "\")/" + fileProperties.getTagLadder() + "[contains(jobdatum,'" + SDF.format(today) + "')])";
                int newCount = count(newQuery);
                fileDetail.setNewJob(newCount);
                String updateQuery = "count(doc(\"" + fileURI + "\")/" + fileProperties.getTagLadder() + "[contains(date_of_last_change,'" + SDF.format(today) + "')])";
                int updateCount = count(updateQuery);
                fileDetail.setUpdateJob(updateCount);
                if (newCount > 0 && updateCount > 0) {
                    fileDetail.setMessage(NEW_UPDATED_JOBS);
                } else if (newCount > 0 && updateCount == 0) {
                    fileDetail.setMessage(NEW_JOBS_ONLY);
                } else if (newCount == 0 && updateCount > 0) {
                    fileDetail.setMessage(UPDATED_JOBS_ONLY);
                } else {
                    fileDetail.setMessage(UNCHANGED_JOBS_ONLY);
                }
                fileDetail.setFileSize(file.length());
                Path path = Paths.get(file.getAbsolutePath());
                BasicFileAttributes view = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
                long uploadedOn = view.creationTime().to(TimeUnit.MILLISECONDS);
                fileDetail.setUploadedOn(uploadedOn);
                fileDetail.setLastModified(file.lastModified());
                fileDetail.setCurrentTime(cTime);
            } catch (IOException | XQException e) {
                LOGGER.info("Exception occured in XMLDirectoryService-generateFileDetail " + FileListException.stackTraceToString(e));
                fileDetail.setJobCount(-1);
                fileDetail.setMessage("Error in task " + fileDetail.getTaskName());
            }
            LOGGER.info("Putting task " + fileDetail);
            MAP_FILE_DETAILS.put(fileName, fileDetail);
        }
    }

    private int count(String query) throws XQException {
        int count = 0;
        XQPreparedExpression exp = fileConnection.getConnection().prepareExpression(query);
        XQResultSequence result = exp.executeQuery();
        if (result.next()) {
            count = Integer.parseInt(result.getItemAsString(null));
        }
        return count;
    }
}
