package io.ensure.deepsea.actuarial.bordereau;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.rxjava.core.Future;

public class BordereauServiceTest {
	
	private static final String BORDEREAULINE_TEST_ID = "bordereauline-test1";
	BordereauService service;

	@Before
	public void setUp() throws Exception {
		service = new TestBordereauServiceImpl();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	BordereauLine getLine() {
		BordereauLine bordereauLine = new BordereauLine();
		bordereauLine.setClientId("client");
		bordereauLine.setCustomerName("customer");
		bordereauLine.setDateSourceCreated(Instant.EPOCH);
		bordereauLine.setEvent(BordereauEvent.INCEPTION);
		bordereauLine.setEventDate(Instant.EPOCH);
		bordereauLine.setIpt(2);
		bordereauLine.setSource("test");
		bordereauLine.setSourceId("test-event1");
		bordereauLine.setStartDate(Instant.EPOCH);
		bordereauLine.setValue(10);
		return bordereauLine;
	}

	@Test
	public void testInitializePersistence() {
		service.initializePersistence(res -> {
			if (res.succeeded()) {
				assertTrue("Initialise Succeeded", true);
			} else {
				fail("Initialise Failed");
			}
		});
	}

	@Test
	public void testAddBordereauLine() {
		service.addBordereauLine(getLine(), res -> {
			if (res.succeeded()) {
				assertEquals("ID's don't match", BORDEREAULINE_TEST_ID, res.result().getBordereauLineId());
			} else {
				fail("Add Bordereau Line Failed");
			}
		});
	}

	@Test
	public void testRetrieveBordereauLine() {
		BordereauLine bordereauLine = getLine();
		bordereauLine.setBordereauLineId(BORDEREAULINE_TEST_ID);
		service.retrieveBordereauLine(BORDEREAULINE_TEST_ID, res -> {
			if (res.succeeded()) {
				assertEquals("BordereauLines don't match", bordereauLine, res.result());
			} else {
				fail("Cannot retrieve bordereauLine");
			}
		});
	}

	@Test
	public void testRetrieveBordereauByClient() {
		service.retrieveBordereauByClient("client", res -> {
			if (res.succeeded()) {
				assertTrue("Wrong size", res.result().size() == 1);
				assertEquals("ID's dont match", "client", res.result().get(0).getClientId());
			} else {
				fail("Cannot retrieve bordereauline by client");
			}
		});
	}

	@Test
	public void testRetrieveBordereauByClientByPage() {
		service.retrieveBordereauByClientByPage("client", 1, res -> {
			if (res.succeeded()) {
				assertTrue("Wrong size", res.result().size() == 1);
				assertEquals("ID's dont match", "client", res.result().get(0).getClientId());
			} else {
				fail("Cannot retrieve bordereauline by client by page");
			}
		});
	}

	@Test
	public void testRequestLastRecordBySource() {
		service.requestLastRecordBySource("test", res -> {
			if (res.succeeded()) {
				assertEquals("Dates don't match", Instant.EPOCH, res.result().getEventDate());
			} else {
				fail("Cannot retrieve last record by source");
			}
		});
	}

	@Test
	public void testRemoveBordereauLine() {
		service.removeBordereauLine(BORDEREAULINE_TEST_ID, res -> {
			if (res.succeeded()) {
				assertTrue("Remove Succeeded", true);
			} else {
				fail("Remove Failed");
			}
		});
	}
	
	class TestBordereauServiceImpl implements BordereauService {

		@Override
		public BordereauService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
			Future<Void> future = Future.future();
			future.setHandler(resultHandler).complete();
			return this;
		}

		@Override
		public BordereauService addBordereauLine(BordereauLine bordereauLine,
				Handler<AsyncResult<BordereauLine>> resultHandler) {
			Future<BordereauLine> future = Future.future();
			bordereauLine.setBordereauLineId(BORDEREAULINE_TEST_ID);
			future.setHandler(resultHandler).complete(bordereauLine);
			return this;
		}

		@Override
		public BordereauService retrieveBordereauLine(String bordereauLineId,
				Handler<AsyncResult<BordereauLine>> resultHandler) {
			Future<BordereauLine> future = Future.future();
			BordereauLine bordereauLine = getLine();
			bordereauLine.setBordereauLineId(BORDEREAULINE_TEST_ID);
			future.setHandler(resultHandler).complete(bordereauLine);
			return this;
		}

		@Override
		public BordereauService retrieveBordereauByClient(String clientId,
				Handler<AsyncResult<List<BordereauLine>>> resultHandler) {
			Future<List<BordereauLine>> future = Future.future();
			List<BordereauLine> bList = new ArrayList<>();
			BordereauLine bordereauLine = getLine();
			bordereauLine.setBordereauLineId(BORDEREAULINE_TEST_ID);
			bList.add(bordereauLine);
			future.setHandler(resultHandler).complete(bList);
			return this;
		}

		@Override
		public BordereauService retrieveBordereauByClientByPage(String clientId, int page,
				Handler<AsyncResult<List<BordereauLine>>> resultHandler) {
			Future<List<BordereauLine>> future = Future.future();
			List<BordereauLine> bList = new ArrayList<>();
			BordereauLine bordereauLine = getLine();
			bordereauLine.setBordereauLineId(BORDEREAULINE_TEST_ID);
			bList.add(bordereauLine);
			future.setHandler(resultHandler).complete(bList);
			return this;
		}

		@Override
		public BordereauService requestLastRecordBySource(String source,
				Handler<AsyncResult<BordereauLine>> resultHandler) {
			Future<BordereauLine> future = Future.future();
			BordereauLine bordereauLine = getLine();
			bordereauLine.setBordereauLineId(BORDEREAULINE_TEST_ID);
			future.setHandler(resultHandler).complete(bordereauLine);
			return this;
		}

		@Override
		public BordereauService removeBordereauLine(String bordereauLineId, Handler<AsyncResult<Void>> resultHandler) {
			Future<Void> future = Future.future();
			future.setHandler(resultHandler).complete();
			return this;
		}
		
	}

}
