package coding.toast.camelplayground.route._0_mqtt_and_mongo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * step 1: simply subscribe to mqtt broker without any code
 */
//@Component
public class MqttToMongoDBRoute_Try1_MQTT_Consumer extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        // manually mqtt publish 하기
        // docker start eclipse-mosquitto
        // docker exec -it eclipse-mosquitto ./mqtt_publish_json.sh 'camel/test/topic'

        // ************ mqtt listener ************
        from("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}")
            .routeId("mqtt-consumer-route")
            .log("Received from MQTT: ${body}")
            ;
    }
}
// https://docs.redhat.com/ko/documentation/red_hat_build_of_apache_camel_for_spring_boot/3.18/html/camel_spring_boot_reference/csb-camel-paho-component-starter
