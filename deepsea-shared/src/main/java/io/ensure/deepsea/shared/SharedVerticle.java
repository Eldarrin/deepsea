package io.ensure.deepsea.shared;

import io.ensure.deepsea.common.BaseMicroserviceVerticle;

public class SharedVerticle extends BaseMicroserviceVerticle {

	@Override
	public void start() {
		vertx.deployVerticle("io.ensure.deepsea.chared.client.ClientVerticle");
	}
}
