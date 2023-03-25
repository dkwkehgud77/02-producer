import com.exam.worker.AvroProducer;
import org.apache.avro.Schema;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("config");
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Avro 포맷의 JSON 파일 읽어서 파싱 후에 Avro 스키마를 생성하고 리스트에 담습니다.
        String jsonFilePath = RESOURCE_BUNDLE.getString("avro.json.file.path");
        List<Schema> avroSchemaList = new ArrayList<>();

        try(FileReader reader = new FileReader(jsonFilePath)){
            // Json 파일 읽어서 JSONArray 형태로 파싱합니다.
            JSONParser parser = new JSONParser();
            JSONArray avroJsonArray = (JSONArray) parser.parse(reader);

            // JSONArray 의 loop 를 돌면서 Avro 스키마를 생성하고 리스트에 추가합니다.
            for(Object obj : avroJsonArray) {
                JSONObject avroSchemaJson = (JSONObject) obj;
                Schema avroSchema = new Schema.Parser().parse(avroSchemaJson.toJSONString());
                avroSchemaList.add(avroSchema);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        final int THREAD_COUNT_PER_PRODUCER = Integer.parseInt(RESOURCE_BUNDLE.getString("thread.count.per.producer"));

        // 생성할 프로듀서 개수 만큼 Thread 를 가지는 스레드 풀을 생성합니다.
        int threadPoolSize = avroSchemaList.size() * THREAD_COUNT_PER_PRODUCER;
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        // 각 AVRO 스키마에 대해 스레드 풀에서 N의 Thread 를 할당합니다.
        for(Schema avroSchema : avroSchemaList) {
            String topicName = avroSchema.getName();
            for (int i = 0; i < THREAD_COUNT_PER_PRODUCER; i++) {
                int threadId = i;
                // 각 Thread 는 프로듀서 객체를 생성하고 대량의 메시지를 병렬로 생산합니다.
                executor.submit(() -> {
                    AvroProducer producer = new AvroProducer();
                    producer.createMessages(threadId, avroSchema);

                    logger.info(String.format("kafka producer started successfully.. topic: %s", topicName));
                });
            }
            try {
                // 메시지 생산 작업이 완료되면 프로듀서 스레드를 종료합니다.
                executor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
                executor.shutdownNow();
            }
        }
    }
}
