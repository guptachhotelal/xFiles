package com.filelist.utils;

import com.filelist.utils.exception.FileListException;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import net.sf.saxon.xqj.SaxonXQDataSource;

public final class FileConnection {

    private static FileConnection fileConnection = null;
    private static XQConnection connection = null;

    public static FileConnection getFileConnection() {
        if (fileConnection == null || connection.isClosed()) {
            fileConnection = new FileConnection();
        }
        return fileConnection;
    }

    private FileConnection() {
        XQDataSource ds = new SaxonXQDataSource();
        try {
            connection = ds.getConnection();
        } catch (XQException e) {
            throw new FileListException(FileListException.stackTraceToString(e));
        }
    }

    public XQConnection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (XQException e) {
            throw new FileListException(FileListException.stackTraceToString(e));
        }
    }
}
