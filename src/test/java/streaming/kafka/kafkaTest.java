package streaming.kafka;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class kafkaTest {
    @Before
    public void setUp(){

    }

    @After
    public void tearDown(){

    }

    @Test
    public void testProducer(){
        new KafkaProducer(KafkaProperties.TOPIC).start();
    }

}
