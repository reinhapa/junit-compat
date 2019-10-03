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

package junitx.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PrivateAccessorTest {
  private String stringField;

  @Test
  public void testPrivateAccessor() {
    new PrivateAccessor();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetField_argumentNull() throws Exception {
    PrivateAccessor.getField(null, null);
  }

  @Test(expected = NoSuchFieldException.class)
  public void testGetField_unkownField() throws Exception {
    PrivateAccessor.getField(new Object(), "someField");
  }

  @Test
  public void testGetField() throws Exception {
    assertEquals(null, PrivateAccessor.getField(this, "stringField"));

    stringField = "someValue";
    assertEquals("someValue", PrivateAccessor.getField(this, "stringField"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetField_argumentsNull() throws Exception {
    PrivateAccessor.setField(null, null, null);
  }

  @Test(expected = NoSuchFieldException.class)
  public void testSetField_unkownField() throws Exception {
    PrivateAccessor.setField(new Object(), "someField", null);
  }

  @Test
  public void testSetField() throws Exception {
    PrivateAccessor.setField(this, "stringField", "myValue");
    assertEquals("myValue", stringField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvoke_argumentNull() throws Throwable {
    PrivateAccessor.invoke(null, null, null, null);
  }

  @Test(expected = NoSuchMethodException.class)
  public void testInvoke_unkownMethod() throws Throwable {
    PrivateAccessor.invoke(new Object(), "someMethod", new Class[0], null);
  }

  @Test
  public void testInvoke() throws Throwable {
    assertEquals("123 true", PrivateAccessor.invoke(this, "theMethod",
        new Class[] {Long.class, Boolean.class}, new Object[] {Long.valueOf(123), Boolean.TRUE}));
  }

  @Test(expected = RuntimeException.class)
  public void testInvoke_withInvocationException() throws Throwable {
    PrivateAccessor.invoke(this, "theFailingMethod", new Class[0], null);
  }

  @SuppressWarnings("unused")
  private String theMethod(Long argument1, Boolean argument2) {
    return String.format("%s %s", argument1, argument2);
  }

  @SuppressWarnings("unused")
  private void theFailingMethod() {
    throw new RuntimeException();
  }
}
