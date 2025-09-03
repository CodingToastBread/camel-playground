package coding.toast.camelplayground.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomSensorDataGeneratingProcessor implements Processor {
    private final static List<String> cities = List.of("Seoul", "Busan", "Tokyo", "Osaka", "Beijing", "Shanghai");

    @Override
    public void process(Exchange exchange) throws Exception {
        HashMap<String, Object> randomSensorData = new HashMap<>();
        randomSensorData.put("type", "sensor");
        randomSensorData.put("city", getRandomCity());
        randomSensorData.put("temperature", getRandomDouble(-10.0, 50.0));
        randomSensorData.put("humidity", getRandomDouble(0.0, 100.0));
        randomSensorData.put("timestamp", createTimestamp());
        exchange.getIn().setBody(randomSensorData);
    }


    private String getRandomCity() {
        return RandomSensorDataGeneratingProcessor.cities.get(ThreadLocalRandom.current().nextInt(RandomSensorDataGeneratingProcessor.cities.size()));
    }

    private double getRandomDouble(double origin, double bound) {
        double value = ThreadLocalRandom.current().nextDouble(origin, bound);
        return Math.round(value * 100.0) / 100.0;
    }

    private String createTimestamp() {
        ZonedDateTime truncatedTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).truncatedTo(ChronoUnit.SECONDS);
        return truncatedTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME); // ex: 2025-09-02T16:38:08+09:00
    }
}
