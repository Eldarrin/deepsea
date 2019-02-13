package io.ensure.deepsea.iam.impl;

import io.ensure.deepsea.iam.Iam;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.oauth2.AccessToken;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class KeyCloakIamImpl implements Iam {

    private Logger log = LoggerFactory.getLogger(getClass());
    
    private static final JsonObject TEMP_CONFIG = new JsonObject()
            .put("keycloak.realm", "master")
            .put("keycloak.auth-server-url", "http://keycloak-deepsea.192.168.99.100.nip.io/auth")
            .put("keycloak.ssl-required", "external")
            .put("keycloak.resource", "deepsea")
            .put("keycloak.credentials.secret", "769a8bff-cfbd-471f-bb9c-fc5db2f29c0c")
            .put("keycloak.use-resource-role-mappings", "true")
            .put("keycloak.confidential-port", 0);

    public AuthProvider getAuthProvider(Vertx vertx, JsonObject config) {
        JsonObject keyCloakJson = new JsonObject();

        keyCloakJson.put("realm", config.getString("keycloak.realm"));
        keyCloakJson.put("auth-server-url", config.getString("keycloak.auth-server-url"));
        keyCloakJson.put("ssl-required", config.getString("keycloak.ssl-required"));
        keyCloakJson.put("resource", config.getString("keycloak.resource"));
        keyCloakJson.put("use-resource-role-mappings", config.getBoolean("keycloak.use-resource-role-mappings"));
        keyCloakJson.put("confidential-port", config.getInteger("keycloak.confidential-port"));
        keyCloakJson.put("credentials",
                new JsonObject().put("secret", config.getString("keycloak.credentials.secret")));

        log.info(keyCloakJson.encodePrettily());

        OAuth2Auth authProvider = KeycloakAuth.create(vertx, OAuth2FlowType.AUTH_CODE, keyCloakJson);

        return authProvider;
    }

    @Override
    public boolean hasAccess(Vertx vertx, AccessToken accessToken, String role) {
        // TODO: take roles
        return false;
    }

}
