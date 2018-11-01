package io.ensure.deepsea.product.impl;

import java.util.List;
import java.util.stream.Collectors;

import io.ensure.deepsea.common.service.MySqlRepositoryWrapper;
import io.ensure.deepsea.product.Product;
import io.ensure.deepsea.product.ProductService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * JDBC implementation of
 * {@link io.vertx.blueprint.microservice.product.ProductService}.
 *
 * @author Eric Zhao
 */
public class MySqlProductServiceImpl extends MySqlRepositoryWrapper implements ProductService {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private static final int PAGE_LIMIT = 10;

	public MySqlProductServiceImpl(Vertx vertx, JsonObject config) {
		super(vertx, config);
	}

	@Override
	public ProductService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		log.info(CREATE_STATEMENT);
		client.getConnection(connHandler(resultHandler, connection -> 
			connection.execute(CREATE_STATEMENT, r -> {
				resultHandler.handle(r);
				connection.close();
			})
		));
		return this;
	}

	@Override
	public ProductService addProduct(Product product, Handler<AsyncResult<Void>> resultHandler) {
		JsonArray params = new JsonArray().add(product.getProductId()).add(product.getClientId()).add(product.getName())
				.add(product.getPrice()).add(product.getIllustration()).add(product.getType());
		executeNoResult(params, INSERT_STATEMENT, resultHandler);
		return this;
	}

	@Override
	public ProductService retrieveProduct(String productId, Handler<AsyncResult<Product>> resultHandler) {
		this.retrieveOne(productId, FETCH_STATEMENT).map(option -> option.map(Product::new).orElse(null))
				.setHandler(resultHandler);
		return this;
	}

	@Override
	public ProductService retrieveProductPrice(String productId, Handler<AsyncResult<JsonObject>> resultHandler) {
		this.retrieveOne(productId, "SELECT price FROM product WHERE productId = ?").map(option -> option.orElse(null))
				.setHandler(resultHandler);
		return this;
	}

	@Override
	public ProductService retrieveProductsByPage(int page, Handler<AsyncResult<List<Product>>> resultHandler) {
		this.retrieveByPage(page, PAGE_LIMIT, FETCH_WITH_PAGE_STATEMENT)
				.map(rawList -> rawList.stream().map(Product::new).collect(Collectors.toList()))
				.setHandler(resultHandler);
		return this;
	}

	@Override
	public ProductService retrieveAllProducts(Handler<AsyncResult<List<Product>>> resultHandler) {
		this.retrieveAll(FETCH_ALL_STATEMENT)
				.map(rawList -> rawList.stream().map(Product::new).collect(Collectors.toList()))
				.setHandler(resultHandler);
		return this;
	}

	@Override
	public ProductService deleteProduct(String productId, Handler<AsyncResult<Void>> resultHandler) {
		this.removeOne(productId, DELETE_STATEMENT, resultHandler);
		return this;
	}

	@Override
	public ProductService deleteAllProducts(Handler<AsyncResult<Void>> resultHandler) {
		this.removeAll(DELETE_ALL_STATEMENT, resultHandler);
		return this;
	}

	// SQL statements

	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `product` (\n"
			+ "  `productId` VARCHAR(60) NOT NULL,\n" + " `clientId` varchar(30) NOT NULL,\n "
			+ "  `name` varchar(255) NOT NULL,\n" + "  `price` double NOT NULL,\n"
			+ "  `illustration` MEDIUMTEXT NOT NULL,\n" + "  `type` varchar(45) NOT NULL,\n"
			+ "  PRIMARY KEY (`productId`)\n" + " )";
	private static final String INSERT_STATEMENT = "INSERT INTO product (`productId`, `clientId`, `name`, `price`, `illustration`, `type`) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String FETCH_STATEMENT = "SELECT * FROM product WHERE productId = ?";
	private static final String FETCH_ALL_STATEMENT = "SELECT * FROM product";
	private static final String FETCH_WITH_PAGE_STATEMENT = "SELECT * FROM product LIMIT ?, ?";
	private static final String DELETE_STATEMENT = "DELETE FROM product WHERE productId = ?";
	private static final String DELETE_ALL_STATEMENT = "DELETE FROM product";
}
