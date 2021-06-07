package org.sample.content.model;

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
public class OrderPayload {

  private String buyer;
  private String description;
  private List<String> items;

  public String getBuyer() {
    return buyer;
  }

  public void setBuyer(String buyer) {
    this.buyer = buyer;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getItems() {
    return items;
  }

  public void setItems(List<String> items) {
    this.items = items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderPayload that = (OrderPayload) o;
    return Objects.equal(buyer, that.buyer) && Objects
        .equal(description, that.description) && Objects
        .equal(items, that.items);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(buyer, description, items);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("buyer", buyer)
        .add("description", description)
        .add("items", items)
        .toString();
  }
}
