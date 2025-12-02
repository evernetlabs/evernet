package org.evernet.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.networknt.schema.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class RelationshipChain {

    public List<String> nodes;

    public String toString() {
        if (nodes == null) {
            return null;
        }

        return Strings.join(nodes, '/');
    }

    public static RelationshipChain fromString(String relationshipChainString) {
        if (StringUtils.isBlank(relationshipChainString)) {
            return RelationshipChain.builder().nodes(null).build();
        }

        String[] nodes = relationshipChainString.split("/");
        return RelationshipChain.builder().nodes(List.of(nodes)).build();
    }
}
