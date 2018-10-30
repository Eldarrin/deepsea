package io.ensure.deepsea.actuarial.bordereau;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vertx.core.json.JsonObject;

public class BordereauLineTest {

	private static final String CUSTOMER_NAME = "customerName";
	private static final String CLIENT_ID = "clientId";
	private static final String LINE_ID = "lineId";
	private BordereauLine bordereauLine;
	private JsonObject jBordereauLine;
	private Instant testInstant;

	@Before
	public void setUp() throws Exception {
		testInstant = Instant.now();
		bordereauLine = new BordereauLine();
		bordereauLine.setBordereauLineId(LINE_ID);
		bordereauLine.setClientId(CLIENT_ID);
		bordereauLine.setCustomerName(CUSTOMER_NAME);
		bordereauLine.setEvent(BordereauEvent.INCEPTION);
		bordereauLine.setEventDate(testInstant);
		bordereauLine.setIpt(2d);
		bordereauLine.setValue(10d);
		bordereauLine.setStartDate(testInstant);

		jBordereauLine = new JsonObject().put("bordereauLineId", LINE_ID).put("clientId", CLIENT_ID)
				.put("customerName", CUSTOMER_NAME).put("event", BordereauEvent.INCEPTION.toString())
				.put("eventDate", testInstant).put("ipt", 2d).put("value", 10d).put("startDate", testInstant);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHashCode() {
		int result = LINE_ID.hashCode();
		result = 31 * result + CLIENT_ID.hashCode();
		assertEquals(result, bordereauLine.hashCode());
	}

	@Test
	public void testBordereauLineBordereauLine() {
		BordereauLine checkBL = new BordereauLine(bordereauLine);
		assertEquals(LINE_ID, checkBL.getBordereauLineId());
		assertEquals(CLIENT_ID, checkBL.getClientId());
		assertEquals(CUSTOMER_NAME, checkBL.getCustomerName());
		assertEquals(BordereauEvent.INCEPTION, checkBL.getEvent());
		assertEquals(testInstant, checkBL.getEventDate());
		assertEquals(2d, checkBL.getIpt(), 0);
		assertEquals(10d, checkBL.getValue(), 0);
		assertEquals(testInstant, checkBL.getStartDate());
	}

	@Test
	public void testBordereauLineJsonObject() {
		BordereauLine jBL;
		jBL = new BordereauLine(jBordereauLine);
		assertEquals(bordereauLine, jBL);
	}

	@Test
	public void testToJson() {
		assertEquals(jBordereauLine, bordereauLine.toJson());
	}

	@Test
	public void testEqualsObject() {
		BordereauLine b = new BordereauLine(bordereauLine);
		assertTrue(b.equals(b));
		assertFalse(b.equals(null));
		assertFalse(b.equals(new String("test")));
		assertEquals(b, bordereauLine);

	}

	@Test
	public void testToString() {
		String checkJson = bordereauLine.toJson().encodePrettily();
		assertEquals(checkJson, bordereauLine.toString());
	}

}
