package org.sample.content.config;

import java.util.List;

import org.sample.content.model.ContentSupplier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ContentSettings {

    private ContentEngine contentEngine;

    private List<ContentSupplier> contentSuppliers;

    @JsonCreator
    public ContentSettings(
            @JsonProperty(value = "providers")
                    List<ContentSupplier> contentSuppliers,
            @JsonProperty(value = "server")
                ContentEngine contentEngine) {
        this.contentEngine = contentEngine;
        this.contentSuppliers = contentSuppliers;

    }

    public ContentEngine getAtmServerSettings() {
        return contentEngine;
    }

    public void setAtmServerSettings(ContentEngine contentEngine) {
        this.contentEngine = contentEngine;
    }

    public List<ContentSupplier> getAtmProviders() {
        return contentSuppliers;
    }

    public void setAtmProviders(List<ContentSupplier> contentSuppliers) {
        this.contentSuppliers = contentSuppliers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentSettings)) {
            return false;
        }
        ContentSettings that = (ContentSettings) o;
        return Objects.equal(getAtmServerSettings(), that.getAtmServerSettings()) && Objects.equal(getAtmProviders(),
                that.getAtmProviders());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAtmServerSettings(), getAtmProviders());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("contentEngine", contentEngine)
                .add("contentSuppliers", contentSuppliers)
                .toString();
    }
}
