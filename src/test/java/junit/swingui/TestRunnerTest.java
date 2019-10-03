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
