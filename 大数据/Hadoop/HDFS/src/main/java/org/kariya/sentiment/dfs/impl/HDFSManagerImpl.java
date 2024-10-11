package org.kariya.sentiment.dfs.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.kariya.sentiment.dfs.HDFSManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description Hadoop
 * @Author Kariya
 * @Date 2024/10/11 13:56
 */
@Slf4j
public class HDFSManagerImpl implements HDFSManager {
    private Configuration conf;
    private FileSystem fs;
    public HDFSManagerImpl() {
        try {
            conf = new Configuration();
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List<String> ls(String path, boolean recursion) {
        try {
            List<String> list = new ArrayList<>();
            RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(new Path(path), recursion);
            while (iterator.hasNext()) {
                LocatedFileStatus ls = iterator.next();
                list.add(ls.getPath().toString());
            }
            return list;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void put(String src, String dest) {
        try {
            fs.copyFromLocalFile(false, true, new Path(src), new Path(dest));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void get(String src, String dest) {
        try {
            fs.copyToLocalFile(false, new Path(src), new Path(dest));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void mkdir(String path) {
        try {
            if (!fs.exists(new Path(path))) {
                fs.mkdirs(new Path(path));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void close() {
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }
}
