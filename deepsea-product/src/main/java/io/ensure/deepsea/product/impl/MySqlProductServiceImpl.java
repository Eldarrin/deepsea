package io.ensure.deepsea.product.impl;

import java.util.List;
import java.util.stream.Collectors;

import io.ensure.deepsea.common.service.MySqlRedisRepositoryWrapper;
import io.ensure.deepsea.product.Product;
import io.ensure.deepsea.product.ProductService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisOptions;

public class MySqlProductServiceImpl extends MySqlRedisRepositoryWrapper implements ProductService {
	
	private static final String PRODUCT = "product";

	private Logger log = LoggerFactory.getLogger(getClass());

	private static final int PAGE_LIMIT = 10;

	public MySqlProductServiceImpl(Vertx vertx, JsonObject config, RedisOptions rOptions) {
		super(vertx, config, rOptions);
		this.typeName = PRODUCT;
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
		log.info("Product Persistence Initialised");
		return this;
	}

	@Override
	public ProductService addProduct(Product product, Handler<AsyncResult<Product>> resultHandler) {
		JsonArray params = new JsonArray().add(product.getClientId()).add(product.getName())
				.add(product.getPrice()).add(product.getIllustration()).add(product.getType());
		this.executeWithCache(params, INSERT_STATEMENT, product.toJson())
			.map(option -> option.map(Product::new).orElse(null))
			.setHandler(resultHandler);
		return this;
	}

	@Override
	public ProductService retrieveProduct(String productId, Handler<AsyncResult<Product>> resultHandler) {
		this.retrieveOneWithCache(getId(productId), FETCH_STATEMENT, productId).map(option -> option.map(Product::new).orElse(null))
				.setHandler(resultHandler);
		return this;
	}


	@Override
	public ProductService retrieveProductPrice(String productId, Handler<AsyncResult<JsonObject>> resultHandler) {
		this.retrieveOne(getId(productId), "SELECT price FROM product WHERE productId = ?").map(option -> option.orElse(null))
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
		this.removeOne(getId(productId), DELETE_STATEMENT, resultHandler);
		return this;
	}

	@Override
	public ProductService deleteAllProducts(Handler<AsyncResult<Void>> resultHandler) {
		this.removeAll(DELETE_ALL_STATEMENT, resultHandler);
		return this;
	}
	
	private Integer getId(String productId) {
		return Integer.parseInt(productId.substring(productId.indexOf('-') + 1));
	}

	// SQL statements

	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `product` (\n"
			+ "  `productId` INT NOT NULL AUTO_INCREMENT,\n" + " `clientId` varchar(30) NOT NULL,\n "
			+ "  `name` varchar(255) NOT NULL,\n" + "  `price` double NOT NULL,\n"
			+ "  `illustration` MEDIUMTEXT NOT NULL,\n" + "  `type` varchar(45) NOT NULL,\n"
			+ "  PRIMARY KEY (`productId`)\n" + " )";
	private static final String INSERT_STATEMENT = "INSERT INTO product (`clientId`, `name`, `price`, `illustration`, `type`) VALUES (?, ?, ?, ?, ?)";
	private static final String FETCH_STATEMENT = "SELECT * FROM product WHERE productId = ?";
	private static final String FETCH_ALL_STATEMENT = "SELECT * FROM product";
	private static final String FETCH_WITH_PAGE_STATEMENT = "SELECT * FROM product LIMIT ?, ?";
	private static final String DELETE_STATEMENT = "DELETE FROM product WHERE productId = ?";
	private static final String DELETE_ALL_STATEMENT = "DELETE FROM product";
}
