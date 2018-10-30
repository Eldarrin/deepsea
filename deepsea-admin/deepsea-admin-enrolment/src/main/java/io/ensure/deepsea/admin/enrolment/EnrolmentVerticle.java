package io.ensure.deepsea.admin.enrolment;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class EnrolmentVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		Future<Void> steps = prepareDatabase().compose(v -> startHttpServer());
		steps.setHandler(startFuture.completer());
	}

	private Future<Void> prepareDatabase() {
		Future<Void> future = Future.future();
		// (...)
		return future;
	}

	private Future<Void> startHttpServer() {
		Future<Void> future = Future.future();
		// (...)
		return future;
	}

}
