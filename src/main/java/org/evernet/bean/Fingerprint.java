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
public class Fingerprint {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(NON_NULL)
    public static class Actor {
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
    public static class Payload {
        @JsonProperty("k")
        private String signingPublicKey;
        @JsonProperty("a")
        private Actor actor;
    }

    @JsonProperty("p")
    private Payload payload;

    @JsonProperty("s")
    private String signature;
}
