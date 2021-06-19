package org.sample.content.enabler;

import static org.sample.content.ContentReponseUtil.recordConsumer;
import static org.sample.content.ContentReponseUtil.handleRequest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.sample.content.config.ContentConstant;
import org.sample.content.config.ContentSettings;
import org.sample.content.liveready.AppCheckHandler;
import org.sample.content.liveready.LifenessReadinessCheck;
import org.sample.content.liveready.ServerStartupListener;
import org.sample.content.liveready.ServerStartupListenerImpl;

/**
 * this is the class that acts as entry point from the client request
 */
public class ContentVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentVerticle.class);

  static KafkaConsumer<String, String> kafkaConsumerBuilder(String bootstrap, String group,
      Vertx vertx) {
    Map<String, String> config = new HashMap<>();
    config.put("bootstrap.servers", bootstrap);
    config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("group.id", group);
    config.put("auto.offset.reset", "earliest");
    config.put("enable.auto.commit", "false");
    return KafkaConsumer.create(vertx, config);
  }

  static KafkaProducer<String, String> kafkaProducerBuilder(Vertx vertx, String boostrap) {
    Map<String, String> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrap);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    config.put("acks", "1");

// use producer for interacting with Apache Kafka
    KafkaProducer<String, String> producer = KafkaProducer.create(vertx, config);
    return producer;
  }

  /**
   * Start the verticle.<p> This is called by Vert.x when the verticle instance is deployed. Don't
   * call it yourself.<p> If your verticle does things in its startup which take some time then you
   * can override this method and call the startFuture some time later when start up is complete.
   *
   * @param startPromise a promise which should be called when verticle start-up is complete.
   */
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);

    try {
      ContentSettings contentSettings = config().mapTo(ContentSettings.class);
      int portNumber = contentSettings.getAtmServerSettings().getHttpport();
      //list all path
      vertx.getOrCreateContext()
          .put(ContentConstant.SERVER_CONFIG.configVal, contentSettings.getAtmServerSettings());

      ServerStartupListener serverStartupListenHandler = new ServerStartupListenerImpl(startPromise,
          portNumber, contentSettings);
      // register readiness and liveness check
      //new AppCheckHandler[]{serverStartupListenHandler}
      LifenessReadinessCheck
          .registerReadinessCheck(router, new AppCheckHandler[]{serverStartupListenHandler});
      LifenessReadinessCheck.registerLivenessCheck(router, null);
      //call kafka
      String bootstrapServer = String
          .format("%s:%s", contentSettings.getAtmServerSettings().getKafkaserver(),
              contentSettings.getAtmServerSettings().getKafkaport());
      /*KafkaConsumer<String, String> kafkaConsumer = kafkaConsumerBuilder(bootstrapServer,
          contentSettings.getAtmServerSettings().getClientgroup(), vertx)
          .handler(recordConsumer::accept);
      KafkaProducer<String,String> kafkaProducer=kafkaProducerBuilder(vertx,bootstrapServer);*/
      contentSettings.getAtmProviders()
          .forEach(js -> router.route().handler(BodyHandler.create()).blockingHandler(
              handleRequest(contentSettings)));
      // create server
      HttpServer server = vertx.createHttpServer();
      server.requestHandler(router).listen(portNumber, serverStartupListenHandler);
    } catch (Exception exception) {
      LOGGER.error("Unexpected error, config " + config(), exception);
    }
  }


}
