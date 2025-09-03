package coding.toast.camelplayground.route._0_mqtt_and_mongo;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Map;

/**
 * step 3: mqtt subscribe -> preprocess -> mongodb insert
 */
//@Component
public class MqttToMongoDBRoute_Try3_PreProcess extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}")
                .routeId("mqtt-consumer-route")
                .log("Received from MQTT: ${body}")
                .log("Inserting into MongoDB collection 'sensorData'...")
                .unmarshal().json(JsonLibrary.Jackson) // InputStream ==> Java Map Object transform
                // ************ pre-process ************
                .process((Exchange exchange) -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> messageBody = exchange.getMessage().getBody(Map.class);
                    String temperature = messageBody.get("temperature").toString();
                    if (Float.parseFloat(temperature) < 20.0) {
                        messageBody.put("temperature", "20");
                    }
                })
                // .process(new TemperatureUnderBoundProcessor())
                .to("mongodb:mongoClient?database=camel_db&collection=sensorData&operation=insert")
                .log("Successfully inserted message into MongoDB.");
    }
}
