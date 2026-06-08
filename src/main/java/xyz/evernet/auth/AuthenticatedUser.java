package xyz.evernet.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticatedUser implements Serializable {

    private String id;

    private String organizationId;

    private Boolean admin;
}