package org.kariya.sentiment.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.kariya.sentiment.args.SentimentOptions;
import org.kariya.sentiment.dfs.HDFSManager;
import org.kariya.sentiment.dfs.impl.HDFSManagerImpl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @Description Hadoop
 * @Author Kariya
 * @Date 2024/10/11 14:15
 */
@Slf4j
public class Task {
    private HDFSManager hdfsManager;
    
    public Task(boolean local) {
        hdfsManager = new HDFSManagerImpl(local);
    }
    
    public void getTask(SentimentOptions options) {
        //判断原始数据目录是否存在
        File sourceFile = new File(options.getSourceDir());
        log.info("上传源文件路径，{}", options.getSourceDir());
        if (!sourceFile.exists()) {
            log.error("{} 要采集的原始数据目录不存在.", options.getSourceDir());
            throw new RuntimeException(options.getSourceDir() + " 要采集的原始数据目录不存在.");
        }
        //读取原始数据目录下的所有文件
        File[] all = sourceFile.listFiles(f -> f.getName().startsWith("caixukun"));
        //判断待上传是否存在
        File tempDir = new File(options.getPendingDir());
        if (!tempDir.exists()) {
            log.info("待上传目录不存在,正在创建中...");
            try {
                FileUtils.forceMkdirParent(tempDir);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //新建任务目录
        File taskDir;
        if (all != null && all.length > 0) {
            taskDir = new File(tempDir, String.format("task_%s", sdf.format(new Date())));
            taskDir.mkdir();
        } else {
            log.info("无上传任务...");
            return;
        }
        
        StringBuffer sb = new StringBuffer();
        //遍历上传文件，在上传目录生成willDoing文件
        Arrays.stream(all).forEach(f -> {
            File destFile = new File(taskDir, f.getName());
            try {
                FileUtils.moveFile(f, destFile);
                sb.append(destFile.getAbsoluteFile() + "\n");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        
        //生成willDoing文件
        String taskName = String.format("willDoing_%s", sdf.format(new Date()));
        try {
            FileUtils.writeStringToFile(new File(tempDir, taskName), sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    public void work(SentimentOptions options) {
        File pendingDir = new File(options.getPendingDir());
        File[] pendingFiles = pendingDir.listFiles(f -> f.getName().startsWith("willDoing") && !f.getName().endsWith("_COPY")
                && !f.getName().endsWith("_DONE"));
        //开始上传
        Arrays.stream(pendingFiles).forEach(f -> {
            File copyTaskFile = new File(f.getAbsoluteFile() + "_COPY");
            try {
                FileUtils.moveFile(f, copyTaskFile);
                String taskDate = f.getName().split("_")[1];
                String hdfsPath = options.getOutput() + String.format("/%s", taskDate);
                //判断目录是否存在，不存在则创建
                hdfsManager.mkdir(hdfsPath);
                log.info("上传文件目标路径，{}", hdfsPath);
                String tasks = FileUtils.readFileToString(copyTaskFile, StandardCharsets.UTF_8);
                Arrays.stream(tasks.split("\n")).forEach(task -> hdfsManager.put(task, hdfsPath));
                //上传成功
                File doneFile = new File(f.getAbsoluteFile() + "_DONE");
                FileUtils.moveFile(copyTaskFile, doneFile);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }
    
    public static void main(String[] args) {
        System.out.println(Math.PI);
    }
}
