package com.filelist.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelist.entity.FileDetail;
import com.filelist.entity.StatusDetail;
import com.filelist.entity.TimeZoneOffset;
import com.filelist.service.DirectoryService;
import com.filelist.utils.FileComparator;
import com.filelist.utils.exception.FileListException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class FileListController {

    private static final Logger LOGGER = LogManager.getLogger(FileListController.class.getName());
    private static boolean watching;

    @Resource
    private TimeZoneOffset timeZoneOffset;

    @Resource
    private StatusDetail statusDetail;
    @Resource
    private DirectoryService directoryService;
    private int dummyCounter;
    private int timerCounter;
    private List<FileDetail> fileDetails;

    @RequestMapping(value = "filelist.htm", method = RequestMethod.GET)
    public String listFiles(final HttpServletRequest request, final HttpServletResponse response, final ModelMap map) {

        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        LOGGER.info("Listing files");
        if (!watching) {
            LOGGER.info("Loading map details for the first time");
            directoryService.generateFileDetail(true);
            LOGGER.info("Initializing DirectoryWatcher thread");
            directoryService.init();
            LOGGER.info("Done initializing DirectoryWatcher thread");
            watching = true;
        }
        response.addHeader("X-Frame-Options", "deny");
        fileDetails = directoryService.getFileDetails();
        LOGGER.info("XML File count" + fileDetails.size());
        int todaysTask = 0;
        int todaysJobCount = 0;
        int done = 0;
        int jobCount = 0;
        int newJob = 0;
        int updateJob = 0;
        int zeroCount = 0;
        int errorCount = 0;

        final Calendar calendar = Calendar.getInstance();
        int cHour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date tDay = calendar.getTime();

        calendar.add(Calendar.DATE, timeZoneOffset.getDayOffset());
        Date pDay = calendar.getTime();

        long cTime = cHour >= timeZoneOffset.getTimeOffset() ? tDay.getTime() : pDay.getTime();
        LOGGER.info("Determing status details");
        List<FileDetail> rList = new ArrayList<>();
        for (FileDetail fileDetail : fileDetails) {
            try {
                File file = new File(fileDetail.getFilePath());
                if (!file.exists()) {
                    rList.add(fileDetail);
                    continue;
                }
                Path path = Paths.get(fileDetail.getFilePath());
                BasicFileAttributes view = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
                long fileCreateTime = view.creationTime().to(TimeUnit.MILLISECONDS);
                long lastModified = file.lastModified();
                if (fileDetail.getJobCount() > -1) {
                    jobCount += fileDetail.getJobCount();
                }
                newJob += fileDetail.getNewJob();
                updateJob += fileDetail.getUpdateJob();
                if (fileDetail.getJobCount() == 0) {
                    ++zeroCount;
                }
                if (fileCreateTime > cTime) {
                    ++todaysTask;
                    if (fileDetail.getJobCount() > -1) {
                        todaysJobCount += fileDetail.getJobCount();
                    }
                }
                if (lastModified >= cTime) {
                    ++done;
                }
                if (fileDetail.getJobCount() == -1) {
                    ++errorCount;
                }
            } catch (IOException e) {
                LOGGER.info("Exception occured in method listFiles " + FileListException.stackTraceToString(e));
                throw new FileListException(FileListException.stackTraceToString(e));
            }
        }
        fileDetails.removeAll(rList);
        int total = fileDetails.size();
        LOGGER.info("Setting status details");
        statusDetail.setTaskCount(total);
        statusDetail.setJobCount(jobCount);
        statusDetail.setNewJob(newJob);
        statusDetail.setUpdateJob(updateJob);
        statusDetail.setUnchangeJob(jobCount - newJob - updateJob);
        statusDetail.setDoneCount(done);
        statusDetail.setPendingCount(total - done);
        statusDetail.setZeroCount(zeroCount);
        statusDetail.setErrorCount(errorCount);
        statusDetail.setTodayTaskCount(todaysTask);
        statusDetail.setTodayJobCount(todaysJobCount);
        map.addAttribute("statusDetail", statusDetail);
        map.addAttribute("lang", locale.getLanguage());
        LOGGER.info("Returning view name filelist.jsp");
        return "filelist";

    }

    @RequestMapping(value = "fetchlist.json", method = RequestMethod.POST)
    public @ResponseBody
    String loadData(final HttpServletRequest request, final HttpServletResponse response) {
        LOGGER.info("Loading/Filtering data via AJAX call");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String searchText = request.getParameter("search[value]") == null ? "" : request.getParameter("search[value]").toLowerCase().trim();
        int length = request.getParameter("length") == null ? 20 : Integer.parseInt(request.getParameter("length"));
        int start = request.getParameter("start") == null ? 0 : Integer.parseInt(request.getParameter("start"));
        int pageNumber = start / length + 1;
        int sortColumn = request.getParameter("order[0][column]") == null ? 1 : Integer.parseInt(request.getParameter("order[0][column]"));
        boolean isAsc = "asc".equals(request.getParameter("order[0][dir]"));
        try {
            ++dummyCounter;
            Map<String, Object> fileMap = new ConcurrentHashMap<>();
            fileMap.put("draw", dummyCounter);
            fileMap.put("recordsTotal", fileDetails.size());
            Collections.sort(fileDetails, new FileComparator(sortColumn, isAsc));
            Map<Integer, List<FileDetail>> map = directoryService.searchFiles(fileDetails, searchText, pageNumber, length == -1 ? fileDetails.size() : length);
            Iterator<Integer> itr = map.keySet().iterator();
            while (itr.hasNext()) {
                Integer keyRecordCount = itr.next();
                fileMap.put("recordsFiltered", keyRecordCount);
                fileMap.put("data", map.get(keyRecordCount));
            }
            LOGGER.info("Returning JSON response via AJAX call");
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(fileMap);
        } catch (JsonProcessingException e) {
            LOGGER.info("Exception occured in method loadData " + FileListException.stackTraceToString(e));
            throw new FileListException(FileListException.stackTraceToString(e));
        }
    }

    @Scheduled(cron = "0 0 0/6 * * ?")
    public void resetTaskDetail() {
        LOGGER.info("Autorefreshing map");
        directoryService.generateFileDetail(true);
        LOGGER.info("Autorefreshing done");
    }

    @RequestMapping(value = "timer.htm", method = RequestMethod.POST)
    public @ResponseBody
    String timer(final HttpServletRequest request, final HttpServletResponse response) {
        ++timerCounter;
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, 1);
        Date nDate = calendar.getTime();
        Date cDate = new Date();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
        String date = df.format(cDate);
        long diff = nDate.getTime() - cDate.getTime();
        String dateString = "<span>" + date + "</span>";
        if (diff > 0 && diff <= 600000 && timerCounter % 2 == 0) {
            dateString = "<span style=\"color: red;\">" + date + "</span>";
        }
        return dateString.substring(0, dateString.lastIndexOf(' '));
    }

    @RequestMapping(value = "taskDetail.json", method = RequestMethod.POST)
    public @ResponseBody
    String taskDetail(final HttpServletRequest request, final HttpServletResponse response) {
        String taskName = request.getParameter("taskName");
        FileDetail fileDetail = directoryService.getFileDetailMap().get(taskName + ".xml");
        try {
            if (fileDetail != null) {
                return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(fileDetail);
            }
        } catch (JsonProcessingException e) {
            LOGGER.info("Exception occured in method taskDetail " + FileListException.stackTraceToString(e));
        }
        return null;
    }

    @RequestMapping(value = "refresh.htm")
    public String refresh(final HttpServletRequest request, final HttpServletResponse response) {

        LOGGER.info("Cleaning and refreshing map");
        directoryService.generateFileDetail(true);
        LOGGER.info("Returning view named filelist.jsp");
        return "redirect:filelist.htm";
    }

}
