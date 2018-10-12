package io.ensure.deepsea.actuarial;

import io.ensure.deepsea.common.BaseMicroserviceVerticle;

public class ActuarialVerticle extends BaseMicroserviceVerticle {

	@Override
	public void start() {
		vertx.deployVerticle("io.ensure.deepsea.actuarial.bordereau.BordereauVerticle");
	}
}
