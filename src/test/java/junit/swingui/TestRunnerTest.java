package junit.swingui;

import org.junit.Test;
import org.junit.runner.RunWith;

import junit.framework.TestCase;
import mockit.Mocked;
import mockit.StrictExpectations;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
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
    new StrictExpectations() {
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
    new StrictExpectations() {
      {
        junit.textui.TestRunner.run(TestCase.class);
      }
    };

    TestRunner.run(TestCase.class);
  }
}
