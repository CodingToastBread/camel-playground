package coding.toast.camelplayground.route._0_mqtt_and_mongo;

import coding.toast.camelplayground.process.RandomSensorDataGeneratingProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.hc.core5.http.HttpHeaders;

import java.util.Map;

/**
 * 8단계: HTTP 결과로 body 를 더 풍성하게 꾸미기 하기
 */
//@Component
public class MqttToMongoDBRoute_Try8_Http_Enrich extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:mqttProducer?period=1000&repeatCount=1")
                .routeId("mqtt-producer-route")
                .process(new RandomSensorDataGeneratingProcessor())
                .marshal().json(JsonLibrary.Jackson)
                .log("publish to mqtt : ${body}")
                .to("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}");

        from("paho:{{mqtt.topic.name}}?brokerUrl={{mqtt.broker.url}}")
                .routeId("mqtt-consumer-route")
                .to("seda:nonblockQueue");

        // https://camel.apache.org/components/4.14.x/seda-component.html
        from("seda:nonblockQueue?concurrentConsumers=10&purgeWhenStopping=true")
                .routeId("seda-consumer-for-non-blocking")
                .unmarshal().json(JsonLibrary.Jackson) // file producer 는 InPutStream 을 받음
                .enrich("direct:enricher", (Exchange originalExchange, Exchange pollExchange) -> {
                    if (pollExchange == null) {
                        return originalExchange;
                    }

                    @SuppressWarnings("unchecked")
                    Map<String, Object> originalBody = originalExchange.getMessage().getBody(Map.class);
                    System.out.println("original = " + originalBody);

                    @SuppressWarnings("unchecked")
                    Map<String, Object> newBody = pollExchange.getMessage().getBody(Map.class);
                    System.out.println("new = " + newBody);

                    originalBody.put("joke", newBody.getOrDefault("joke", "none"));
                    return originalExchange;

                })
                .log("Successfully Enriched ${body}");

        // https://alanhohn.com/posts/2016/camel-enricher-3/
        from("direct:enricher")
                .setHeader(HttpHeaders.ACCEPT, constant("application/json"))
                .setHeader(Exchange.HTTP_URI, constant("https://icanhazdadjoke.com/"))
                .transform().simple("${null}")
                .to("http://ignored").unmarshal().json(JsonLibrary.Jackson);
    }

}
