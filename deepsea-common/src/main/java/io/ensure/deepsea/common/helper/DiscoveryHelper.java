package io.ensure.deepsea.common.helper;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.rxjava.core.Future;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Status;

public class DiscoveryHelper {
	
	private final Vertx vertx;
	
	public DiscoveryHelper(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void getStatus(String serviceName, Status status, Handler<AsyncResult<Boolean>> resultHandler) {
		Future <Boolean> future = Future.future();
		ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
		
		discovery.getRecords(record -> record.getName().equals(serviceName), ar -> {
			if (ar.succeeded()) {
				for (Record rec : ar.result()) {
					future.setHandler(resultHandler)
						.complete(rec.getStatus().equals(status));
				}
			} else {
				future.setHandler(resultHandler).fail(ar.cause());
			}
		});

	}

}
