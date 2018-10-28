package io.ensure.deepsea.actuarial;

import io.vertx.core.AbstractVerticle;

public class ActuarialVerticle extends AbstractVerticle {

	@Override
	public void start() {
		vertx.deployVerticle("io.ensure.deepsea.actuarial.bordereau.BordereauVerticle");
	}
}
