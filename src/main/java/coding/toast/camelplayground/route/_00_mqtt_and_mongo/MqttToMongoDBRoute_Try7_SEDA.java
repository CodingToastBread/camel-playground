package coding.toast.camelplayground.route._00_mqtt_and_mongo;

import coding.toast.camelplayground.process.HeavyOperateProcessor;
import coding.toast.camelplayground.process.RandomSensorDataGeneratingProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * step 7 : heavy operation ? use seda (Staged event-driven architecture)!
 */
//@Component
public class MqttToMongoDBRoute_Try7_SEDA extends RouteBuilder {

    @Override
    public void configure() throws Exception {


        List<String> cities = List.of("Seoul", "Busan", "Tokyo", "Osaka", "Beijing", "Shanghai");
        from("timer:mqttProducer?period=1000")
                .routeId("mqtt-producer-route")
                .process(new RandomSensorDataGeneratingProcessor())
                .marshal().json(JsonLibrary.Jackson)
                .log("publish to mqtt : ${body}")
                .to("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}");

        from("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}")
                .routeId("mqtt-consumer-route")
//                .to("seda:nonblockQueue");
                 .to("direct:nonblockQueue");

        // https://camel.apache.org/components/4.14.x/seda-component.html
         from("direct:nonblockQueue")
//        from("seda:nonblockQueue?concurrentConsumers=10&purgeWhenStopping=true")
                .routeId("seda-consumer-for-non-blocking")
                // Pseudo Heavy Operation
                .process(new HeavyOperateProcessor(Duration.ofSeconds(3)))
                /*
                // preprocess
                .unmarshal().json(JsonLibrary.Jackson)
                .process(new TemperatureUnderBoundProcessor())

                // rabbitMQ publish
                .marshal().json(JsonLibrary.Jackson)
                .to("spring-rabbitmq:amq.direct?queues=sensor-data" +
                        "&routingKey=sensor-data" +
                        "&arg.exchange.autoDelete=false")

                // mongodb insert
                .to("mongodb:mongoClient?database=camel_db&collection=sensorData&operation=insert")
                .log("Successfully inserted message into MongoDB.")

                // create file
                .marshal().json(JsonLibrary.Jackson) // file producer 는 InPutStream 을 받음
                .to("file:data/sensor-data?fileName=sensor-data-${date:now:yyyyMMdd_HHmmss}.json".trim())
                .log("Successfully save to file");
                */
        ;
    }
}
