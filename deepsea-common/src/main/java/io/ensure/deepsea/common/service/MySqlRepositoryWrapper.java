package io.ensure.deepsea.common.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.asyncsql.MySQLClient;

/**
 * Helper and wrapper class for MySQL repository services.
 *
 * @author Andy Ward (prev. Eric Zhao)
 */
public class MySqlRepositoryWrapper {
	
	protected final SQLClient client;

	public MySqlRepositoryWrapper(Vertx vertx, JsonObject config) {
		this.client = MySQLClient.createShared(vertx, config);
	}

	/**
	 * Suitable for `add`, `exists` operation.
	 *
	 * @param params        query params
	 * @param sql           sql
	 * @param resultHandler async result handler
	 */
	protected void executeNoResult(JsonArray params, String sql, Handler<AsyncResult<Void>> resultHandler) {
		client.getConnection(connHandler(resultHandler, connection -> 
			connection.updateWithParams(sql, params, r -> {
				if (r.succeeded()) {
					resultHandler.handle(Future.succeededFuture());
				} else {
					resultHandler.handle(Future.failedFuture(r.cause()));
				}
				connection.close();
			})
		));
	}

	protected <R> void execute(JsonArray params, String sql, R ret, Handler<AsyncResult<R>> resultHandler) {
		client.getConnection(connHandler(resultHandler, connection -> 
			connection.updateWithParams(sql, params, r -> {
				if (r.succeeded()) {
					resultHandler.handle(Future.succeededFuture(ret));
				} else {
					resultHandler.handle(Future.failedFuture(r.cause()));
				}
				connection.close();
			})
		));
	}
	
	protected Future<Optional<Integer>> executeReturnKey(JsonArray params, String sql) {
		return getConnection().compose(connection -> {
			Future<Optional<Integer>> future = Future.future();
			connection.updateWithParams(sql, params, r -> {
				if (r.succeeded()) {
					future.complete(Optional.of(r.result().getKeys().getInteger(0)));
				} else {
					future.fail(r.cause());
				}
				connection.close();
			});
			return future;
		});
	}
			
			

	protected <K> Future<Optional<JsonObject>> retrieveOne(K param, String sql) {
		return getConnection().compose(connection -> {
			Future<Optional<JsonObject>> future = Future.future();
			connection.queryWithParams(sql, new JsonArray().add(param), r -> {
				if (r.succeeded()) {
					List<JsonObject> resList = r.result().getRows();
					if (resList == null || resList.isEmpty()) {
						future.complete(Optional.empty());
					} else {
						future.complete(Optional.of(resList.get(0)));
					}
				} else {
					future.fail(r.cause());
				}
				connection.close();
			});
			return future;
		});
	}

	protected int calcPage(int page, int limit) {
		if (page <= 0)
			return 0;
		return limit * (page - 1);
	}

	protected Future<List<JsonObject>> retrieveByPage(int page, int limit, String sql) {
		return retrieveManyByPage(page, limit, null, sql);
	}

	protected Future<List<JsonObject>> retrieveManyByPage(int page, int limit, JsonArray params, String sql) {
		if (params != null) {
			params.add(calcPage(page, limit)).add(limit);
		} else {
			params = new JsonArray().add(calcPage(page, limit)).add(limit);
		}
		JsonArray paramq = params;
		return getConnection().compose(connection -> {
			Future<List<JsonObject>> future = Future.future();
			connection.queryWithParams(sql, paramq, r -> {
				if (r.succeeded()) {
					future.complete(r.result().getRows());
				} else {
					future.fail(r.cause());
				}
				connection.close();
			});
			return future;
		});
	}
	
	protected Future<List<JsonObject>> retrieveMany(JsonArray param, String sql) {
		return getConnection().compose(connection -> {
			Future<List<JsonObject>> future = Future.future();
			connection.queryWithParams(sql, param, r -> {
				if (r.succeeded()) {
					future.complete(r.result().getRows());
				} else {
					future.fail(r.cause());
				}
				connection.close();
			});
			return future;
		});
	}

	protected Future<List<JsonObject>> retrieveAll(String sql) {
		return getConnection().compose(connection -> {
			Future<List<JsonObject>> future = Future.future();
			connection.query(sql, r -> {
				if (r.succeeded()) {
					future.complete(r.result().getRows());
				} else {
					future.fail(r.cause());
				}
				connection.close();
			});
			return future;
		});
	}

	protected <K> void removeOne(K id, String sql, Handler<AsyncResult<Void>> resultHandler) {
		client.getConnection(connHandler(resultHandler, connection -> {
			JsonArray params = new JsonArray().add(id);
			connection.updateWithParams(sql, params, r -> {
				if (r.succeeded()) {
					resultHandler.handle(Future.succeededFuture());
				} else {
					resultHandler.handle(Future.failedFuture(r.cause()));
				}
				connection.close();
			});
		}));
	}

	protected void removeAll(String sql, Handler<AsyncResult<Void>> resultHandler) {
		client.getConnection(connHandler(resultHandler, connection -> 
			connection.update(sql, r -> {
				if (r.succeeded()) {
					resultHandler.handle(Future.succeededFuture());
				} else {
					resultHandler.handle(Future.failedFuture(r.cause()));
				}
				connection.close();
			})
		));
	}

	/**
	 * A helper methods that generates async handler for SQLConnection
	 *
	 * @return generated handler
	 */
	protected <R> Handler<AsyncResult<SQLConnection>> connHandler(Handler<AsyncResult<R>> h1,
			Handler<SQLConnection> h2) {
		return conn -> {
			if (conn.succeeded()) {
				final SQLConnection connection = conn.result();
				h2.handle(connection);
			} else {
				h1.handle(Future.failedFuture(conn.cause()));
			}
		};
	}

	protected Future<SQLConnection> getConnection() {
		Future<SQLConnection> future = Future.future();
		client.getConnection(future);
		return future;
	}
	
	protected String fromInstant(Instant instant) {
		Date myDate = Date.from(instant);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return dateFormat.format(myDate);
	}



}
