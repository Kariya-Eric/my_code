package org.kariya.sentiment.args;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description Hadoop
 * @Author Kariya
 * @Date 2024/10/11 11:22
 */
@Setter
@Getter
public class SentimentOptions extends OptionsBase {
    @Option(name = "help", abbrev = 'h', help = "打印帮助信息", defaultValue = "true")
    public boolean help;
    @Option(name = "source", abbrev = 's', help = "要采集的数据位置", defaultValue = "")
    public String sourceDir;
    @Option(name = "pending", abbrev = 'p', help = "生成待上传的上传目录", defaultValue = "")
    public String pendingDir;
    @Option(name = "output", abbrev = 'o', help = "生成要上传到的HDFS路径", defaultValue = "")
    public String output;
    
}
