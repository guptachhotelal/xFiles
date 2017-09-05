package com.filelist.listener;

import com.filelist.utils.FileConnection;
import java.util.Set;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(ContextListener.class.getName());

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();
        for (Thread thread : setOfThread) {
            if (thread.getName().startsWith("directoryWatcher") || thread.getName().startsWith("runnerThread")) {
                thread.interrupt();
                break;
            }
        }
        FileConnection.getFileConnection().closeConnection();
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
    }
}
