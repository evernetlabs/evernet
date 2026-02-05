package org.evernet.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class Payload<T> {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(NON_NULL)
    public static class ActorDetails {
        @JsonProperty("n")
        private String displayName;
        @JsonProperty("d")
        private String description;
        @JsonProperty("t")
        private String type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(NON_NULL)
    public static class Actor {
        @JsonProperty("k")
        private String signingPublicKey;
        @JsonProperty("d")
        private ActorDetails details;
    }

    @JsonProperty("a")
    private Actor actor;

    @JsonProperty("s")
    private String signature;

    @JsonProperty("d")
    private T data;
}
