package coding.toast.camelplayground.route._0_mqtt_and_mongo;

import coding.toast.camelplayground.process.RandomSensorDataGeneratingProcessor;
import coding.toast.camelplayground.process.TemperatureUnderBoundProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 * step 6: [mqtt publish] + [mqtt subscribe -> preprocess -> mongodb insert + save to file + publish to rabbitMQ]
 */
//@Component
public class MqttToMongoDBRoute_Try6_Pub_MQTT extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:mqttProducer?period=1000")
                .routeId("mqtt-producer-route")
                .process(new RandomSensorDataGeneratingProcessor())
                .marshal().json(JsonLibrary.Jackson)
                .log("Publish To MQTT : ${body}")
                .to("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}");


        from("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}")
                .routeId("mqtt-consumer-route")
                .log("Received from MQTT: ${body}")
                .unmarshal().json(JsonLibrary.Jackson) // InputStream ==> Java Map Object 로 변환

                // 전처리
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
    }
}
