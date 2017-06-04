package com.linkedkeeper.log.agent.local.filechannel.manager;

import com.linkedkeeper.log.agent.config.QueueConstant;
import com.linkedkeeper.log.agent.config.SenderConfig;
import com.linkedkeeper.log.agent.utils.PrintUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

public class DataFileManager {

    private final String dataPath;
    private final String dataFileName;
    private final TreeSet<String> files;
    private final TreeSet<String> oldFiles;

    public DataFileManager(String queueName) {
        this.dataPath = SenderConfig.getInstance().getDataPath() + "/";
        this.dataFileName = queueName + QueueConstant.DATA_FILE_SUFFIX;
        this.files = new TreeSet<String>();
        this.oldFiles = new TreeSet<String>();

        this.loadFiles();
    }

    private void loadFiles() {
        File dir = new File(dataPath);
        if (!dir.exists()) {
            return;
        }
        String[] nameList = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.indexOf(dataFileName) != -1);
            }
        });
        for (String fileName : nameList) {
            String fullPath = dataPath + fileName;
            files.add(fullPath);
        }
    }

    public synchronized String createRotationFile() {
        long count = 0;
        if (!files.isEmpty()) {
            String lastFileName = files.last();
            count = Long.valueOf(lastFileName.substring(lastFileName.lastIndexOf(".") + 1)).longValue() + 1;
        } else if (!oldFiles.isEmpty()) {
            String lastFileName = oldFiles.last();
            count = Long.valueOf(lastFileName.substring(lastFileName.lastIndexOf(".") + 1)).longValue() + 1;
        }

        File dir = new File(dataPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Cannot create data directory: " + dataPath);
        }

        String newFileName = dataPath + dataFileName + "." + String.format("%019d", count);
        files.add(newFileName);
        return newFileName;
    }

    public synchronized boolean findFile(String findName) {
        boolean isFound = false;
        if (findName == null) {
            return isFound;
        }

        List<String> tempFileList = new ArrayList<String>();
        String _fileName;
        while ((_fileName = files.pollFirst()) != null) {
            tempFileList.add(_fileName);
            if (_fileName.equals(findName)) {
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            for (String tempFile : tempFileList) {
                files.add(tempFile);
            }
        } else {
            for (String tempFile : tempFileList) {
                oldFiles.add(tempFile);
            }
        }
        return isFound;
    }

    public synchronized String pollFirstFile() {
        String fileName = files.pollFirst();
        if (fileName != null) {
            oldFiles.add(fileName);
        }
        return fileName;
    }

    public synchronized void deleteOlderFilers(String filePath) {
        if (oldFiles.size() == 0) {
            return;
        }

        String first = oldFiles.first();
        if (first.equals(filePath)) {
            return;
        }

        NavigableSet<String> subSet = oldFiles.subSet(first, true, filePath, false);
        List<String> deleteList = new ArrayList<String>();
        for (String deletePath : subSet) {
            if (new File(deletePath).delete() == false) {
                PrintUtil.error("Delete the old file " + deletePath + " failed.");
            } else {
                deleteList.add(deletePath);
            }
        }

        // Delete from tree
        for (String path : deleteList) {
            oldFiles.remove(path);
        }
    }

    public synchronized boolean isEmpty() {
        return files.isEmpty();
    }
}
