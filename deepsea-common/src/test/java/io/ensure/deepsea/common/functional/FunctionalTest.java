package io.ensure.deepsea.common.functional;

import io.ensure.deepsea.common.functional.Functional;
import io.vertx.core.Future;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for {@link Functional}.
 */
public class FunctionalTest {

  @Test
  public void testSequenceFuture() throws Exception {
    List<Future<Integer>> list1 = Arrays.asList(Future.succeededFuture(2),
      Future.succeededFuture(9), Future.succeededFuture(17));
    Future<List<Integer>> fs1 = Functional.allOfFutures(list1);
    List<Integer> cpList = Arrays.asList(2, 9, 17);
    assertEquals(true, fs1.succeeded());
    assertEquals(cpList, fs1.result());

    List<Future<Integer>> list2 = Arrays.asList(Future.succeededFuture(2),
      Future.failedFuture("Oops"), Future.succeededFuture(17));
    Future<List<Integer>> fs2 = Functional.allOfFutures(list2);
    assertEquals(false, fs2.succeeded());
    assertEquals("Oops", fs2.cause().getMessage());
  }

}