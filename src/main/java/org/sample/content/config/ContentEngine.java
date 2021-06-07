package org.sample.content.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ContentEngine {

  private int httpport;
  private String kafkaserver;
  private int kafkaport;
  private String clientgroup;
  private List<String> topics;

  public int getHttpport() {
    return httpport;
  }

  public List<String> getTopics() {
    return topics;
  }

  public String getKafkaserver() {
    return kafkaserver;
  }

  public int getKafkaport() {
    return kafkaport;
  }

  public String getClientgroup() {
    return clientgroup;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentEngine that = (ContentEngine) o;
    return httpport == that.httpport && kafkaport == that.kafkaport && Objects
        .equal(kafkaserver, that.kafkaserver) && Objects
        .equal(clientgroup, that.clientgroup) && Objects
        .equal(topics, that.topics);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(httpport, kafkaserver, kafkaport, clientgroup, topics);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("httpport", httpport)
        .add("kafkaserver", kafkaserver)
        .add("kafkaport", kafkaport)
        .add("clientgroup", clientgroup)
        .add("topics", topics)
        .toString();
  }
}
