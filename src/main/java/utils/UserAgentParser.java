package utils;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import cz.mallat.uasparser.UserAgentInfo;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class UserAgentParser {

    public static UASparser uaSparser = null;

    /**
     * 它是随着类的加载而执行，只执行一次，并优先于主函数。
     * 具体说，静态代码块是由类调用的。类调用时，先执行静态代码块，然后才执行主函数的。
     * 静态代码块其实就是给类初始化的，而构造代码块是给对象初始化的。
     * 静态代码块中的变量是局部变量，与普通函数中的局部变量性质没有区别。
     * 一个类中可以有多个静态代码块
     * 不需要实例化就可以调用
     */
    static {
        try {
            uaSparser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取解析后的ua
     * @param ua
     * @return
     */
    public static UserAgent getUserAgent(String ua){
        UserAgent userAgent = null;

        if (StringUtils.isNotEmpty(ua)){
            try {
                UserAgentInfo parse = uaSparser.parse(ua);
                if (parse != null){
                    userAgent = new UserAgent();
                    userAgent.setBrowserName(parse.getUaFamily());
                    userAgent.setBrowserVersion(parse.getBrowserVersionInfo());
                    userAgent.setOsName(parse.getOsFamily());
                    userAgent.setOsVersion(parse.getOsName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return userAgent;
    }

    public static void main(String[] args) throws Exception {
//        UserAgentInfo info = uaSparser.parse("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; )");
//        System.out.println(info);

        String string = UserAgentParser.getUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; )").toString();
        System.out.println(string);
    }

}
