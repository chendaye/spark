package streaming.log;

import org.apache.log4j.Logger;

/**
 * 日志生成
 */
public class LoggerGenerator {
    private static Logger logger = Logger.getLogger(LoggerGenerator.class.getName());

    public static void main(String[] args) throws Exception{

        int index = 0;
        while(true) {
            Thread.sleep(1000);
            logger.info("value : " + index++);
        }
    }
}
