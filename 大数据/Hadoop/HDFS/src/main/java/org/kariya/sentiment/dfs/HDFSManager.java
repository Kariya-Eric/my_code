package org.kariya.sentiment.dfs;

import java.util.List;

/**
 * @Description Hadoop
 * @Author Kariya
 * @Date 2024/10/11 13:54
 */
public interface HDFSManager {
    //读取列表
    List<String> ls(String path, boolean recursion);
    
    //上传文件到指定位置
    void put(String src, String dest);
    
    //下载到指定位置
    void get(String src, String dest);
    
    void mkdir(String path);
    
    void close();
}
