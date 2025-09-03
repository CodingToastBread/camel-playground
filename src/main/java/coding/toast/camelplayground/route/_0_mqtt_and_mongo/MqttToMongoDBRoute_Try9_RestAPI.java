package coding.toast.camelplayground.route._0_mqtt_and_mongo;

import coding.toast.camelplayground.process.RandomSensorDataGeneratingProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.hc.core5.http.HttpHeaders;

/**
 * step 9: 마지막으로 rest dsl 을 활용해서 외부에 rest api 를 노출시킵니다.<br>
 * - localhost:8080/api/mqtt-pub [POST] : 해당 rest api 가 mqtt broker 에 rest api 로 전달받은 json 을 publish 합니다.<br>
 * - localhost:8080/api/sample-data [GET] : mqtt-pub 에서 http Body 에 넣는 json 포맷 예시를 제공합니다.<br>
 */
//@Component
public class MqttToMongoDBRoute_Try9_RestAPI extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration().component("netty-http")
                .host("0.0.0.0").port(8080)
                .bindingMode(RestBindingMode.off);

        rest("/api")
                .get("/sample-data").to("direct:sample-data")
                .post("/mqtt-pub").to("direct:mqtt-pub");

        from("direct:sample-data")
        .process(new RandomSensorDataGeneratingProcessor())
                .marshal().json(JsonLibrary.Jackson)
                .setHeader(HttpHeaders.CONTENT_TYPE,  constant("application/json"))
                .log("Sending Response ${body}");

        from("direct:mqtt-pub")
                .log("direct:mqtt-pub - body : ${body}")
                .to("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}")
                .setBody().simple("${null}") // http response body 비우기
        ;

    }
}
