package dmit2015.faces;

import dmit2015.restclient.KeycloakLoginMpRestClient;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Named
@SessionScoped
public class LoginSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    @RestClient
    private KeycloakLoginMpRestClient _loginRestClient;
    @Inject
    @ConfigProperty(name = "keycloak.oidc.clientId")
    private String oidcClientId;

    @Inject
    @ConfigProperty(name = "keycloak.oidc.clientSecret")
    private String oidcClientSecret;

    @Getter @Setter
    private String username;

    @Getter @Setter
    private JsonObject loginResponsePayload;

    @Getter @Setter
    private LocalDateTime expiresIn;

    public String getAuthorization() {
        return "Bearer " + loginResponsePayload.getString("access_token");
    }


    public List<String> getRoles() {
        if (loginResponsePayload == null ||
                !loginResponsePayload.containsKey("access_token")) {
            return Collections.emptyList();
        }

        try {
            String accessToken =
                    loginResponsePayload.getString("access_token");

            String[] parts = accessToken.split("\\.");

            if (parts.length < 2) {
                return Collections.emptyList();
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            try (JsonReader reader =
                         Json.createReader(new StringReader(payloadJson))) {

                JsonObject payload = reader.readObject();

                if (!payload.containsKey("realm_access")) {
                    return Collections.emptyList();
                }

                JsonObject realmAccess =
                        payload.getJsonObject("realm_access");

                JsonArray roles =
                        realmAccess.getJsonArray("roles");

                if (roles == null) {
                    return Collections.emptyList();
                }

                return roles.stream()
                        .map(value -> value.toString().replace("\"", ""))
                        .toList();
            }

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }


    public String checkForToken() {
        String nextPage = null;

        if (loginResponsePayload == null) {
            nextPage = "/login?faces-redirect=true";
        } else {
            // Check if token has expired
            if (LocalDateTime.now().isAfter(expiresIn)) {
                // get a new token
                String refreshToken = loginResponsePayload.getString("refresh_token");
                loginResponsePayload = _loginRestClient.refreshToken(refreshToken, oidcClientId, oidcClientSecret,"refresh_token");
            }
        }

        return nextPage;
    }
}