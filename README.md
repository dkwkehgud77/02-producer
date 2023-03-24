
# 프로듀서

### 1. infra 세팅
- Zookeeper, Kafka, Schema-Registry, MySQL 생성
- AVRO 스키마, Kafka 토픽, MySql 테이블 생성

```bash
cd realtime-pipeline/02-infra

# kafka admin 
http://localhost:9000/

# message create
mvn clean package
mvn exec:java

# kafka topic dataset1
http://localhost:9000/topic/dataset1

```

