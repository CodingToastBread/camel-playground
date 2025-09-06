package coding.toast.camelplayground.route._01_split;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//@Component
public class CsvSplit extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:data/split-data?fileName=customers-100.csv&noop=true")
            .split(body().tokenize("\n")).streaming()
                // 첫째 줄은 Header 여서 Skip!
                .filter(simple("${exchangeProperty.CamelSplitIndex} > 0"))
                    .log("Read line: ${body}")
                .end() // filter 종료
            .end() // split 종료
            .log("all line Read Finish!")
        ;
    }
}
