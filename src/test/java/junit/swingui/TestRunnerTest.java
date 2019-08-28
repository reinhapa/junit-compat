package junit.swingui;

import org.junit.Test;

import junit.framework.TestCase;
import mockit.Expectations;
import mockit.Mocked;


public class TestRunnerTest {
  @Mocked
  junit.textui.TestRunner testRunner;

  @Test
  public void testTestRunner() {
    new TestRunner();
  }

  @Test
  public void testMain() {
    String[] args = new String[] {"arg1", "arg2"};
    new Expectations() {
      {
        junit.textui.TestRunner.main(args);
      }
    };

    TestRunner.main(args);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRun_noTestCaseClass() {
    TestRunner.run(Object.class);
  }

  @Test
  public void testRun() {
    new Expectations() {
      {
        junit.textui.TestRunner.run(TestCase.class);
      }
    };

    TestRunner.run(TestCase.class);
  }
}
