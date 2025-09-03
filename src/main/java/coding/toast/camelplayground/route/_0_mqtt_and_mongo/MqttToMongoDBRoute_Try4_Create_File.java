package coding.toast.camelplayground.route._0_mqtt_and_mongo;

import coding.toast.camelplayground.process.TemperatureUnderBoundProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 * step 4: mqtt subscribe -> preprocess -> mongodb insert + save to file
 */
//@Component
public class MqttToMongoDBRoute_Try4_Create_File extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}")
                .routeId("mqtt-consumer-route")
                .log("Received from MQTT: ${body}")
                .log("Inserting into MongoDB collection 'sensorData'...")
                .unmarshal().json(JsonLibrary.Jackson) // InputStream ==> Java Map Object 로 변환

                // 전처리
                .process(new TemperatureUnderBoundProcessor())

                // mongodb insert
                .to("mongodb:mongoClient?database=camel_db&collection=sensorData&operation=insert")
                .log("Successfully inserted message into MongoDB.")

                // create file
                .marshal().json(JsonLibrary.Jackson) // file producer 는 InPutStream 을 받음
                .to("file:data/sensor-data?fileName=sensor-data-${date:now:yyyyMMdd_HHmmss}.json".trim())
                .log("Successfully save to file");
    }
}
