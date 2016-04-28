package junit.swingui;

import junit.framework.TestCase;

public class TestRunner {

  public static void main(String[] args) {
    junit.textui.TestRunner.main(args);
  }

  public static void run(Class<?> clazz) {
    if (!TestCase.class.isAssignableFrom(clazz)) {
      throw new IllegalArgumentException("Class is not a testcase");
    }
    @SuppressWarnings("unchecked")
    Class<TestCase> testClass = (Class<TestCase>) clazz;
    junit.textui.TestRunner.run(testClass);
  }
}
