package net.reini.junit;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CustomTestSuiteTest {
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {}

  @Test
  public void testSuite() {
  //  fail("Not yet implemented");
  }

}
