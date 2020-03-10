package sparkoffline.hbase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import com.ggstar.util.ip.IpHelper;
import streaming.project.utils.IPParser;

public class TestApp {

    @Before
    public void setUp(){

    }

    @After
    public void tearDown(){

    }

    @Test
    public void ipdatabaseTestIP(){
//        String ip = "110.85.18.234";
//        String region = IpHelper.findRegionByIp(ip);
//        System.out.println(region);
    }

    @Test
    public void testIP(){
        // 123.116.60.97
        IPParser.RegionInfo regionInfo = IPParser.getInstance().analyseIp("110.85.18.234");
        System.out.println(regionInfo.getCity());
        System.out.println(regionInfo.getCountry());
        System.out.println(regionInfo.getProvince());
    }
}
