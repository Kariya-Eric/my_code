package org.kariya;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @Description Hadoop
 * @Author Kariya
 * @Date 2024/10/10 16:27
 */
public class HDFSClientTest {
    private static Configuration con = null;
    private static FileSystem fs = null;
    
    @Before
    public void connectHDFS() throws IOException {
        //设置客户端的身份，以具备权限hdfs上进行操作
        System.setProperty("HADOOP_USER_NAME", "root");
        //创建配置对象实例
        con = new Configuration();
        con.set("fs.defaultFS", "hdfs://192.168.232.101:8020");
        //创建文件操作系统
        fs = FileSystem.get(con);
    }
    
    @After
    public void closeHDFS() {
        //关闭hdfs链接
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    //创建文件夹
    @Test
    public void mkdir() throws IOException {
        Path path = new Path("/java");
        //判断文件夹是否存在
        if (!fs.exists(path)) {
            fs.mkdirs(path);
        }
    }
    
    //@Test
    //public void download() throws IOException {
    //    Path source = new Path("/source/weibo/comment_log/20190810_kariya001/caixukun.csv");
    //    Path target = new Path("D:\\data\\caixukun.csv");
    //    fs.copyToLocalFile(source, target);
    //}
    
    @Test
    public void upload() throws IOException {
        Path target = new Path("/java");
        Path source = new Path("D:\\data\\1.txt");
        fs.copyFromLocalFile(source, target);
    }
    
    @Test
    public void show(){
        String name = fs.getHomeDirectory().getName();
        System.out.println(name);
    }
}
