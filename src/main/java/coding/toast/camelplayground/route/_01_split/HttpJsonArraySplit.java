package coding.toast.camelplayground.route._01_split;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * 낙서장입니다!
 */
//@Component
public class HttpJsonArraySplit extends RouteBuilder {
    /// https://freeformatter.com/cron-expression-generator-quartz.html
    @Override
    public void configure() throws Exception {

        // https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=estern&logNo=110010101624
//        from("quartz://myGroup/myTimerName?cron=0 * * ? * * *")
        from("timer:foo?repeatCount=1")
                // 10명의 사용자 정보 조회, json array 형태
                .to("https://jsonplaceholder.typicode.com/users")

                // json 배열 형태를 모두
                .split().jsonpath("$")
                    // split 때문에 독립적인 Exchange 생성되고, 모든 Body 타입은 LinkedHashMap 이 생성된다.
                    .log("${exchangeId} + ${body.class.name}")

                    // Body 내용 확인
                    .log("${body}")

                    // MongoDB 에 insert
                    .to("mongodb:mongoClient?database=myDatabase&collection=users&operation=insert")
                .end() // split sub-route 종료, split(), choice(), multicast() 등에서 사용됨
                .log("ALL JSON DATA INSERT FINISH!")
        ;

    }
}
