package coding.toast.camelplayground.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

/**
 * 센서 데이터가 20 도 미만이면 무조건 20 으로 맞춘다.
 */
public class TemperatureUnderBoundProcessor implements Processor {

    @Override
    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> messageBody = exchange.getMessage().getBody(Map.class);
        String temperature = messageBody.get("temperature").toString();
        if (Float.parseFloat(temperature) < 20.0) {
            messageBody.put("temperature", "20");
        }
    }
}
