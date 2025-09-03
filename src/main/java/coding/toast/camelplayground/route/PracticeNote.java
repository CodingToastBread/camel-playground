package coding.toast.camelplayground.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 낙서장입니다!
 */
//@Component
public class PracticeNote extends RouteBuilder {

    // https://docs.redhat.com/ko/documentation/red_hat_build_of_apache_camel_for_spring_boot/3.18/html/camel_spring_boot_reference/csb-camel-paho-component-starter
    @Override
    public void configure() throws Exception {

        /*
         1초 마다 새로운 랜덤 센서 데이터(json format) 을 생성하여 mqtt 서버에 전송
         ex: {"city":"Shanghai","temperature":4.07,"humidity":78.74,"type":"sensor","timestamp":"2025-09-02T16:54:04+09:00"}
         */
        List<String> cities = List.of("Seoul", "Busan", "Tokyo", "Osaka", "Beijing", "Shanghai");
        from("timer:mqttProducer?period=1000")
            .routeId("mqtt-producer-route")
            .process(exchange -> {
                HashMap<String, Object> randomSensorData = new HashMap<>();
                randomSensorData.put("type", "sensor");
                randomSensorData.put("city", getRandomCity(cities));
                randomSensorData.put("temperature", getRandomDouble(-10.0, 50.0));
                randomSensorData.put("humidity", getRandomDouble(0.0, 100.0));
                randomSensorData.put("timestamp", createTimestamp());
                exchange.getIn().setBody(randomSensorData);
            })
            .marshal().json(JsonLibrary.Jackson)
            .log("${body}")
            .to("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}");

        /*
        MQTT 서버로 부터 메시지를 다시 받는다.
        샘플 받기: docker exec -it eclipse-mosquitto ./mqtt_publish_json.sh 'camel/data'
         */
        from("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}")
            .routeId("mqtt-consumer-route")
            .log("Received from MQTT: ${body}")
            .log("Inserting into MongoDB collection 'sensorData'...")
            .unmarshal().json(JsonLibrary.Jackson)
//            .process(exchange -> {
//                @SuppressWarnings("unchecked")
//                Map<String, Object> messageBody = exchange.getMessage().getBody(Map.class);
//                String temperature = messageBody.get("temperature").toString();
//                if (Float.parseFloat(temperature) < 20.0) {
//                    messageBody.put("temperature", "20");
//                }
//            })
            .to("mongodb:mongoClient?database=camel_db&collection=sensorData&operation=insert")
            .log("Successfully inserted message into MongoDB.")

//            .marshal().json(JsonLibrary.Jackson) // file producer 는 InPutStream 을 받음
//            .to("file:data/sensor-data?fileName=sensor-data-${date:now:yyyyMMdd_HHmmss}.json".trim())
//            .log("Successfully save to file")


//            .marshal().json(JsonLibrary.Jackson)
//            .to("spring-rabbitmq:amq.direct?queues=sensor-data" +
//                      "&routingKey=sensor-data" +
//                      "&arg.exchange.autoDelete=false");
        ;

        // https://hub.docker.com/_/mongo
        // docker run -d --name mongo -p  27017:27017 -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=secret mongo:latest
        // docker exec -it mongo mongosh -u admin -p secret
        // db.sensorData.find().sort({timestamp:-1}).limit(3)
        // db.sensorData.countDocuments()
    }

    private static String getRandomCity(List<String> cities) {
        return cities.get(ThreadLocalRandom.current().nextInt(cities.size()));
    }

    private static double getRandomDouble(double origin, double bound) {
        double value = ThreadLocalRandom.current().nextDouble(origin, bound);
        return Math.round(value * 100.0) / 100.0;
    }

    private static String createTimestamp() {
        ZonedDateTime truncatedTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).truncatedTo(ChronoUnit.SECONDS);
        return truncatedTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME); // ex: 2025-09-02T16:38:08+09:00
    }
}
