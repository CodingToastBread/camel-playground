package coding.toast.camelplayground.process;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.time.Duration;
import java.util.Map;

@Slf4j
public class HeavyOperateProcessor implements Processor {

    private final Duration duration;

    public HeavyOperateProcessor(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Map body = exchange.getMessage().getBody(Map.class);
        log.info("HEAVY OPERATE START ==> {}", body);
        Thread.sleep(duration.toMillis());
        log.info("HEAVY OPERATE END ==> {}", body);
    }
}
