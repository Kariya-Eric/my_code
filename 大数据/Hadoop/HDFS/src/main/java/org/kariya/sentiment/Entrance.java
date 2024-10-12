package org.kariya.sentiment;

import com.google.devtools.common.options.OptionsParser;
import lombok.extern.slf4j.Slf4j;
import org.kariya.sentiment.args.SentimentOptions;
import org.kariya.sentiment.task.Task;

import java.util.Collections;

/**
 * @Description Hadoop
 * @Author Kariya
 * @Date 2024/10/11 13:49
 */
@Slf4j
public class Entrance {
    
    public static void main(String[] args) {
        OptionsParser parser = OptionsParser.newOptionsParser(SentimentOptions.class);
        parser.parseAndExitUponError(args);
        SentimentOptions options = parser.getOptions(SentimentOptions.class);
        if (options.getSourceDir().isEmpty() || options.getOutput().isEmpty()) {
            printUsage(parser);
            return;
        }
        //本地文件系统方式
        //Task task = new Task(true);
        //HDFS文件系统方式
        Task task = new Task(false);
        log.info("舆情上报程序启动...");
        task.getTask(options);
        log.info("正在上传数据到HDFS...");
        task.work(options);
        log.info("数据上传完成...");
    }
    
    private static void printUsage(OptionsParser parser) {
        log.info("Usage:java -jar sentiment.jar OPTIONS");
        log.info(parser.describeOptions(Collections.EMPTY_MAP, OptionsParser.HelpVerbosity.LONG));
    }
}
