/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015, 2019 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junit.framework.TestSuite;
import junit.swingui.TestRunnerTest;
import junitx.framework.AssertTest;
import junitx.util.PrivateAccessorTest;
import net.reini.junit.CustomTestSuiteTest;
import net.reini.junit.NetReiniJunitTest;
import net.reini.junit.RecursiveTestSuiteBuilder;
import net.reini.junit.pkg1.NetReiniJunitPkg1_FirstTest;
import net.reini.junit.pkg1.NetReiniJunitPkg1_SecondTest;
import net.reini.junit.pkg2.NetReiniJunitPkg2Test;

public class RecursiveTestSuiteBuilderTest {

  @Test
  public void testDefaultPackage() throws Exception {
    // a list of expected test classes
    List<Class<?>> testClasses = new ArrayList<>();
    testClasses.add(TestRunnerTest.class);
    testClasses.add(AssertTest.class);
    testClasses.add(PrivateAccessorTest.class);
    testClasses.add(CustomTestSuiteTest.class);
    testClasses.add(NetReiniJunitTest.class);
    testClasses.add(NetReiniJunitPkg1_FirstTest.class);
    testClasses.add(NetReiniJunitPkg1_SecondTest.class);
    testClasses.add(NetReiniJunitPkg2Test.class);
    testClasses.add(DefaultPackageTest.class);
    testClasses.add(RecursiveTestSuiteBuilderTest.class);

    // build the suite
    TestSuite rootSuite = new TestSuite("root suite");
    RecursiveTestSuiteBuilder.build(RecursiveTestSuiteBuilderTest.class, rootSuite);

    // walk the suite, removing every encountered test class
    walkSuite(rootSuite, testClasses);
    assertTrue("test classes not in the suite: " + testClasses, testClasses.isEmpty());
  }


  private void walkSuite(TestSuite suite, List<Class<?>> testClasses)
      throws ClassNotFoundException {
    int testCount = suite.testCount();
    for (int i = 0; i < testCount; i++) {
      junit.framework.Test test = suite.testAt(i);
      if (test instanceof TestSuite) {
        walkSuite((TestSuite) test, testClasses);
      } else {
        Class<?> testClass = Class.forName(test.toString());
        assertTrue("unexpected test class " + testClass.getName(), testClasses.contains(testClass));
        testClasses.remove(testClass);
      }
    }
  }
}
