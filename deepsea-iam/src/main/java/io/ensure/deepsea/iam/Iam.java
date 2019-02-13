package io.ensure.deepsea.iam;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.oauth2.AccessToken;

public interface Iam {

    public AuthProvider getAuthProvider(Vertx vertx, JsonObject config);

    public boolean hasAccess(Vertx vertx, AccessToken accessToken, String role);

}
