# ** 프로젝트에서 세팅 방법 **

## 🎯 컨테이너 생성

[container](container) 디렉토리에 들어가서 아래와 같이 container 를 실행시킵니다.

```bash
docker compose up
```

<br>

모든 컨테이너가 정상 구동이 됐다면 창을 아래처럼 가상의 센서데이터를 mqtt broker 에<br>
publish 하는 명령어를 입력합니다.

```bash
docker exec -it eclipse-mosquitto ./mqtt_publish_json.sh 'camel/test/topic'
```

<br>

> **참고 (1): mongosh 접속법**<br>
>
> ```bash
> # 정상 실행 후 아래처럼 명령어 입력해서 mongosh 열어보기
> docker exec -it mongo mongosh -u admin -p secret
> use camel_db
> db.sensorData.find().sort({timestamp:-1}).limit(3)
> ```

<br>

>**참고 (2): RabbitMQ Admin UI 접속법**<br>
브라우저에서  http://localhost:15672/#/queues 로 접속하시고,<br>
로그인합니다. 로그인 계정은 `guest/guest` 입니다.<br>

<br><br><br>

## 🎯 프로젝트 실행방식

이후에는 [PlaygroundApplication.java](src/main/java/coding/toast/camelplayground/PlaygroundApplication.java) 에서 main 문을 실행해서<br>
Spring boot 애플리케이션을 실행하면 됩니다.<br>

다만 현재 [_0_mqtt_and_mongo](src/main/java/coding/toast/camelplayground/route/_0_mqtt_and_mongo) 패키지에 있는 Camel Route 클래스들이<br>
`@Component` 애노테이션이 모두 주석된 상태입니다. <br>

MqttToMongoDBRoute_Try1 ~ 9 까지 Route 클래스가 있는데,
Try?? 의 번호 순서대로 `@Component` 애노테이션이 주석을 한번씩 풀어서 Spring boot 애플리케이션을
실행해보면 됩니다.

```
아래와 같은 방법으로 차례대로 실행하면 됩니다.
1. Try1 @Component 주석 해제
2. Try2 @Component 주석 해제, Try1 @Component 다시 주석 처리
3. Try3 @Component 주석 해제, Try2 @Component 다시 주석 처리 
```

<br><br><br>

## 참고한 것들

- https://camel.apache.org/manual/java-dsl.html
- https://camel.apache.org/components/4.14.x/paho-component.html
- https://camel.apache.org/components/4.14.x/mongodb-component.html
- https://camel.apache.org/components/4.14.x/seda-component.html
- https://camel.apache.org/components/4.14.x/http-component.html
- https://camel.apache.org/components/4.14.x/dataformats/jackson-dataformat.html
- https://camel.apache.org/components/4.14.x/languages/simple-language.html
- https://alanhohn.com/posts/2016/camel-enricher-3/
- https://www.masterspringboot.com/camel/apache-camel-rest-example-for-beginners/
- https://stackoverflow.com/questions/58266688/how-to-create-a-queue-in-rabbitmq-upon-startup

