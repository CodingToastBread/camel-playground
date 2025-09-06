package coding.toast.camelplayground.route._02_dataformat;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.stereotype.Component;


//@Component
public class CsvDataFormatExample extends RouteBuilder {

    // https://camel.apache.org/components/4.14.x/dataformats/csv-dataformat.html#_marshalling_a_map_to_csv

    @Override
    public void configure() throws Exception {
        // CSV DataFormat 객어 생성
        // 클래스 이름과 걙아서 FQCN 사용
        CsvDataFormat csv = new CsvDataFormat();
        csv.setCaptureHeaderRecord(true);
        csv.setSkipHeaderRecord(true);
        csv.setDelimiter(',');

        from("file:data/split-data?fileName=customers-100.csv&noop=true")
            .routeId("csv-data-format")
            .unmarshal(csv)
            .log("CSV Header : ${header[CamelCsvHeaderRecord]}")
            .split(body())
                .log("${body}")
            .end()
            ;

    }
}
