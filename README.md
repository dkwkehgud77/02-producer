
# Kafka Producer Application

## Introduction
이 프로젝트는 대량의 Avro 형식의 메시지를 분산 병렬 처리하는 Java 기반 Kafka Consumer 애플리케이션입니다.
이 애플리케이션은 장애 복구 시에도 데이터 유실 없이 Exactly-Once Delivery를 보장합니다.
또한, Backpressure 기능을 지원하여 Consumer가 메시지를 처리하는 속도를 제어할 수 있습니다.


## Features
### Kafka 대량의 메시지 분산 병렬 처리

### 데이터 유실 없이 Exactly-Once Delivery 보장

### Backpressure 기능으로 메시지 처리하는 속도 제어


# Getting Started
### Prerequisites
- Java JDK 11
- Apache Maven 3.9.0
- Apache Kafka 2.8.1


### Configuration
프로퍼티 파일에 Kafka Producer 애플리케이션에 필요한 구성 설정을 합니다.
```properties
# Json
avro.json.file.path=../schema/schema_avro.json

# Thread
thread.count.per.producer=5

# Message
message.count.per.topic=300

# Kafka
kafka.bootstrap.servers=localhost:9091,localhost:9092,localhost:9093
kafka.schema.registry.url=http://localhost:8081

```

### Application Start
1. properties 파일을 세팅합니다.
2. Maven을 사용하여 프로젝트를 빌드합니다.
3. Maven을 사용하여 Consumer 애플리케이션을 실행합니다.
```bash
$ mvn clean compile exec:java

[INFO] --- compiler:3.1:compile (default-compile) @ producer ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 2 source files to /Users/dohyung/ailab/realtime-pipeline/02-producer/target/classes
[INFO] 
[INFO] --- exec:3.0.0:java (default-cli) @ producer ---
[Main.main()] INFO Main - kafka producer application start...

```

### Application Deployment
1. properties 파일을 세팅합니다.
2. Maven을 사용하여 프로젝트를 패키징합니다.
3. Java를 사용하여 백그라운드로 Producer 애플리케이션을 실행합니다.
```bash
$ mvn clean compile
$ nohup java -jar target/producer-1.0-jar-with-dependencies.jar &

$ ps -ef |grep producer
  501 15323 10877   0  2:10PM ttys019    0:05.73 /usr/bin/java -jar target/producer-1.0-jar-with-dependencies.jar

```

### Application Stop
Mac이나 리눅스 기반의 OS에서는 Shell 파일을 이용해서 애플리케이션을 실행하거나 중지할 수 있습니다.
```bash
$ chmod 755 start.sh stop.sh
$ ./stop.sh

[1] + killed     nohup java -jar target/producer-1.0-jar-with-dependencies.jar
```

