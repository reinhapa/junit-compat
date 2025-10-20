/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015, 2020 Patrick Reinhart
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


import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

@MockitoSettings
public class TestRunnerTest {
  @Mock
  MockedStatic<junit.textui.TestRunner> testRunner;

  @Test
  void testTestRunner() {
    assertThatNoException().isThrownBy(TestRunner::new);
  }

  @Test
  void testMain() {
    String[] args = new String[] {"arg1", "arg2"};

    assertThatNoException().isThrownBy(() -> TestRunner.main(args));

    testRunner.verify(() -> junit.textui.TestRunner.main(args));
  }

  @Test
  void testRun_noTestCaseClass() {
    assertThatIllegalArgumentException().isThrownBy(() -> TestRunner.run(Object.class));
  }

  @Test
  void testRun() {
    assertThatNoException().isThrownBy(() -> TestRunner.run(TestCase.class));

    testRunner.verify(() -> junit.textui.TestRunner.run(TestCase.class));
  }
}
