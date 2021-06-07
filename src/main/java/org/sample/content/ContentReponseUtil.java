package org.sample.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;
import java.util.Optional;
import java.util.function.Consumer;
import org.sample.content.config.ContentSettings;
import org.sample.content.model.ContentSupplier;
import org.sample.content.model.OrderPayload;

public class ContentReponseUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentReponseUtil.class);

  static ObjectMapper objectMapper = new ObjectMapper();

  public static String toJsonString(Object waypointStat) {
    try {
      return objectMapper.writeValueAsString(waypointStat);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return "{}";
  }

  public static <T> T fromJSON(final TypeReference<T> type,
      final String jsonPacket) {
    T data = null;

    try {
      data = new ObjectMapper().readValue(jsonPacket, type);
    } catch (Exception e) {
      // Handle the problem
    }
    return data;
  }

  public static String getJsonString(String jsonString, String field) {
    try {
      return objectMapper.readTree(jsonString).get(field).toString();
    } catch (JsonProcessingException jsonProcessingException) {
      jsonProcessingException.printStackTrace();
    }
    return null;
  }


  public static Handler<RoutingContext> handleRequest(ContentSettings contentSettings,
      KafkaConsumer<String, String> kafkaConsumer, KafkaProducer<String, String> kafkaProducer) {
    return rc -> {
      //TODO: input validation
      //Map<String, String> paths = rc.get(AtmConfig.PATHS.configVal);
      String uri = rc.request().uri();

      if (HttpMethod.POST.equals(rc.request().method())) {
        handlePost(contentSettings, uri, rc, kafkaProducer);
        //call kafka producer
      } else if (HttpMethod.GET.equals(rc.request().method())) {
        Optional<ContentSupplier> atmProviderOptional = contentSettings.getAtmProviders().stream()
            .filter(p -> p.getPath().equals(uri)).findFirst();
        if (atmProviderOptional.isPresent()) {
          ContentSupplier contentSupplier = atmProviderOptional.get();
          //Future.succeededFuture(new JsonObject().put("hello", "world"));
          LOGGER.info("serving the request with path " + uri);
          rc.response().setStatusCode(HttpResponseStatus.OK.code())
              .putHeader("Content-Type", "application/json")
              .end(ContentReponseUtil.toJsonString(contentSupplier));
          //search over topic config

        } else {
          rc.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
              .end(ContentReponseUtil.toJsonString(contentSettings.getAtmProviders()));
        }
      } else {
        rc.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
      }
    };

  }

  private static void handlePost(ContentSettings contentSettings, String uri, RoutingContext rc,
      KafkaProducer<String, String> kafkaProducer) {
    Optional<ContentSupplier> atmProviderOptional = contentSettings.getAtmProviders().stream()
        .filter(p -> p.getPath().equals(uri)).findFirst();
    if (atmProviderOptional.isPresent()) {
      ContentSupplier contentSupplier = atmProviderOptional.get();
      //search over topic config
      String strBody = rc.getBodyAsString();
      OrderPayload orderPayload = Json.decodeValue(strBody, OrderPayload.class);
      LOGGER.info("serving POST for " + uri + " body=" + strBody);
      KafkaProducerRecord<String, String> producerRecord = KafkaProducerRecord
          .create(contentSettings.getAtmServerSettings().getTopics().get(0),
              orderPayload.getBuyer(), strBody);
      kafkaProducer.send(producerRecord).onSuccess(metadataHandler);
      LOGGER.info(
          "data sent to Kafka topic " + contentSettings.getAtmServerSettings().getTopics().get(0));
      rc.response().setStatusCode(HttpResponseStatus.CREATED.code()).end();
    } else {
      rc.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
    }
  }

  public static Consumer<KafkaConsumerRecord<String, String>> recordConsumer = consumerRecord -> LOGGER
      .info(
          "processing key=" + consumerRecord.key() + " value=" + consumerRecord.value()
              + " partition=" + consumerRecord.partition()
              + " offset=" + consumerRecord.offset());
  public static Handler<RecordMetadata> metadataHandler = event -> LOGGER
      .info(
          "topic=" + event.getTopic() + " offset=" + event.getOffset()
              + " partition=" + event.getPartition());
}
