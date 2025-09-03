package coding.toast.camelplayground.route._0_mqtt_and_mongo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 * step 2: mqtt subscribe -> mongodb insert
 *        need to add camel-mongodb-starter dependency inside pom.xml
 */
//@Component
public class MqttToMongoDBRoute_Try2_MongoDb_Insert extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}")
                .routeId("mqtt-consumer-route")
                .log("Received from MQTT: ${body}")
                .log("Inserting into MongoDB collection 'sensorData'...")
                .unmarshal().json(JsonLibrary.Jackson)
                // ************ mongodb insert ************
                .to("mongodb:mongoClient?database=camel_db&collection=sensorData&operation=insert")
                .log("Successfully inserted message into MongoDB.");
    }
}
