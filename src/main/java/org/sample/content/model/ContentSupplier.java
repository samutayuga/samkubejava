package org.sample.content.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ContentSupplier {

    private String id;

    private String path;

    private String[] items;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentSupplier)) {
            return false;
        }
        ContentSupplier that = (ContentSupplier) o;
        return Objects.equal(getId(), that.getId())
                && Objects.equal(getPath(), that.getPath())
                && Objects.equal(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getPath(), getItems());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("path", path)
                .add("items", items)
                .toString();
    }
}
