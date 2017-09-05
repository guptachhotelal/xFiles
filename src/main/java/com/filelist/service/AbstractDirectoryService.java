package com.filelist.service;

import com.filelist.entity.FileDetail;
import com.filelist.utils.exception.FileListException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractDirectoryService implements DirectoryService {

	protected static final Logger							LOGGER				= LogManager.getLogger(DirectoryService.class.getName());
	@Resource
	private Path											feedDir;
	public static final ConcurrentMap<String, FileDetail>	MAP_FILE_DETAILS	= new ConcurrentHashMap<>();

	@Override
	public void init()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try (WatchService watchService = feedDir.getFileSystem().newWatchService();)
				{
					DirectoryWatcher directoryWatcher = new DirectoryWatcher(watchService);
					Thread thread = new Thread(directoryWatcher, "directoryWatcher");
					thread.start();
					feedDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
					thread.join();
				}
				catch(IOException | InterruptedException e)
				{
					LOGGER.info("Exception occured in DirectoryService-init " + FileListException.stackTraceToString(e));
				}
			}
		}, "runnerThread").start();
	}

	public void removeFromMap(String fileName)
	{
		LOGGER.info("Removing feed file from map");
		MAP_FILE_DETAILS.remove(fileName);
	}

	public List<FileDetail> getFileDetails()
	{
		LOGGER.info("Returning list of filedetail from map");
		return new ArrayList<FileDetail>(MAP_FILE_DETAILS.values());
	}

	public Map<String, FileDetail> getFileDetailMap()
	{
		return MAP_FILE_DETAILS;
	}

	public Map<Integer, List<FileDetail>> searchFiles(List<FileDetail> fileDetails, String sText, int pageNumber, int size)
	{
		LOGGER.info("Loading/Searching/Filtering filedetails");
		Map<Integer, List<FileDetail>> map = new HashMap<Integer, List<FileDetail>>();
		int count = 0;
		int idx = (pageNumber - 1) * size;
		List<FileDetail> pageFiles = new ArrayList<FileDetail>();
		List<FileDetail> filteredFiles = new ArrayList<FileDetail>();
		for(FileDetail fileDetail : fileDetails)
		{
			if(fileDetail.getTaskName().toLowerCase().contains(sText) || fileDetail.getTaskName().matches(sText) || fileDetail.getMessage() != null && fileDetail.getMessage().toLowerCase().startsWith(sText))
			{
				filteredFiles.add(fileDetail);
			}
		}
		for(; idx < filteredFiles.size(); idx++)
		{
			FileDetail fileDetail = filteredFiles.get(idx);
			++count;
			if(count <= size)
			{
				pageFiles.add(fileDetail);
			}
		}
		map.put(filteredFiles.size(), pageFiles);
		return map;
	}

	class DirectoryWatcher implements Runnable
	{
		private WatchService watchService;

		public DirectoryWatcher(WatchService watchService)
		{
			this.watchService = watchService;
		}

		@Override
		public void run()
		{
			try
			{
				WatchKey watchKey = watchService.take();
				while(watchKey != null)
				{
					List<WatchEvent<?>> events = watchKey.pollEvents();
					for(WatchEvent<?> event : events)
					{
						String taskName = event.context().toString();
						if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
						{
							System.out.println("Feed created: " + taskName);
							addEditMap(taskName, false);
						}
						else if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
						{
							System.out.println("Feed Updated: " + taskName);
							addEditMap(taskName, true);
						}
						else if(event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
						{
							System.out.println("Feed delete: " + taskName);
							removeFromMap(taskName);
						}
						else
						{
							System.out.println("Feed overflow: " + taskName);
							generateFileDetail(true);
						}
					}
					watchKey.reset();
					watchKey = watchService.take();
				}
			}
			catch(InterruptedException e)
			{
				LOGGER.info("Exception occured in DirectoryWatcher-run " + FileListException.stackTraceToString(e));
			}
		}
	}

}
