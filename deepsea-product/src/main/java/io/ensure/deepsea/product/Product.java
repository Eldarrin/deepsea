package io.ensure.deepsea.product;

import io.ensure.deepsea.shared.product.ProductConverter;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Product {

	private String productId;
	private String clientId;
	private String name;
	private double price = 0.0d;
	private String illustration;
	private String type;

	public Product() {
		// Empty constructor
	}

	public Product(Product other) {
		this.productId = other.productId;
		this.clientId = other.clientId;
		this.name = other.name;
		this.price = other.price;
		this.illustration = other.illustration;
		this.type = other.type;
	}

	public Product(JsonObject json) {
		ProductConverter.fromJson(json, this);
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		ProductConverter.toJson(this, json);
		return json;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getIllustration() {
		return illustration;
	}

	public void setIllustration(String illustration) {
		this.illustration = illustration;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Product product = (Product) o;

		return productId.equals(product.productId) && clientId.equals(product.clientId);
	}

	@Override
	public int hashCode() {
		int result = productId.hashCode();
		result = 31 * result + clientId.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.toJson().encodePrettily();
	}

}
