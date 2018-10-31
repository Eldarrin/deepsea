package io.ensure.deepsea.shared;

import io.vertx.core.AbstractVerticle;

public class SharedVerticle extends AbstractVerticle {

	@Override
	public void start() {
		vertx.deployVerticle("io.ensure.deepsea.shared.client.ClientVerticle");
		//vertx.deployVerticle("io.ensure.deepsea.shared.product.ProductVerticle");
	}
}