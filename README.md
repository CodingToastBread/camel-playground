# ** 프로젝트에서 세팅 방법 **

## 🎯 컨테이너 생성부터 시작!

### 1. MQTT Broker
```bash
docker run -d -v "./container/mosquitto/config:/mosquitto/config" \
      -v "./container/mosquitto/script/mqtt_publish_json.sh:/mqtt_publish_json.sh" \
      -p 1883:1883 --name eclipse-mosquitto eclipse-mosquitto:2.0.22

# 정상적으로 실행 된 후에는 아래처럼 명령어 실행 해두기. 
# 이러면 1~3초 랜덤한 간격으로 센서 데이터 생성하여 camel/test/topic 토픽에 publish 한다.
docker exec -it eclipse-mosquitto ./mqtt_publish_json.sh 'camel/test/topic'
```

<br><br>

### 2. MongoDB

```bash
# mongodb container 실행
docker run -d --name mongo -p 27017:27017 \ 
        -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=secret \
        mongo:8.0.13

# 정상 실행 후 아래처럼 명령어 입력해서 mongosh 열어보기
docker exec -it mongo mongosh -u admin -p secret
```
<br>

이후에 센서 데이터를 mongodb 에 넣게 되는데, 이것을 확인하려면 아래처럼 mongosh 명령어를 입력

```bash
use camel_db
db.sensorData.find().sort({timestamp:-1}).limit(3)
```

<br><br>

### 3. RabbitMQ

```bash
docker run -d -v "./container/rabbitmq/config/rabbitmq.conf:/etc/rabbitmq/rabbitmq.conf" \
        -v "./container/rabbitmq/config/definitions.json:/etc/rabbitmq/definitions.json" \
        --name rabbitmq -p 5673:5672 -p 15673:15672 rabbitmq:3.13-management
# 설정 파일(definitions.json)을 통해서 계정(id: guest , pw :guest) 가 생기고,
# sensor-data 라는 queue 도 생성합니다.
```
RabbitMQ 컨테이너 실행 후, 브라우저에서  http://localhost:15672/#/queues 로 접속해보시기 바랍니다.<br>
이후 로그인도 진행해주세요. 로그인 계정은 `guest/guest` 입니다.<br>
마지막으로 로그인 후에 Queue 에서 sensor-data 가 정상적으로 생겼는지 확인합니다. (아래그림 참고)

![msedge_PVDZ0lK2co.png](README_img/msedge_PVDZ0lK2co.png)

<br><br><br><br>

## 🎯 프로젝트 실행방식

이후에는 [PlaygroundApplication.java](src/main/java/coding/toast/camelplayground/PlaygroundApplication.java) 에서 main 문을 실행해서<br>
Spring boot 애플리케이션을 실행하면 됩니다.<br>

다만 현재 [_0_mqtt_and_mongo](src/main/java/coding/toast/camelplayground/route/_0_mqtt_and_mongo) 패키지에 있는 Camel Route 클래스들이<br>
모두 `@Component` 애노테이션이 모두 주석된 상태입니다. <br>

MqttToMongoDBRoute_Try1 ~ 9 까지 Route 클래스가 있는데,
Try? 의 번호 순서대로 `@Component` 애노테이션이 주석을 한번씩 풀어서 Spring boot 애플리케이션을
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

