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

package junitx.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class PrivateAccessor {

  public static Object invoke(Object object, String name, Class<?>[] argumentTypes,
      Object[] arguments) throws Throwable {
    if (object == null) {
      throw new IllegalArgumentException("Invalid null object argument");
    }
    Class<?> cls = object.getClass();
    while (cls != null) {
      try {
        Method method = cls.getDeclaredMethod(name, argumentTypes);
        method.setAccessible(true);
        return method.invoke(object, arguments);
      } catch (InvocationTargetException e) {
        /*
         * if the method throws an exception, it is embedded into an InvocationTargetException.
         */
        throw e.getTargetException();
      } catch (Exception ex) {
        /*
         * in case of an exception, we will throw a new NoSuchFieldException object
         */
      }
      cls = cls.getSuperclass();
    }
    throw new NoSuchMethodException(
        "Failed method invocation: " + object.getClass().getName() + "." + name + "()");
  }

  public static Object getField(Object object, String name) throws NoSuchFieldException {
    if (object == null) {
      throw new IllegalArgumentException("Invalid null object argument");
    }
    for (Class<?> cls = object.getClass(); cls != null; cls = cls.getSuperclass()) {
      try {
        Field field = cls.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
      } catch (Exception ex) {
        /*
         * in case of an exception, we will throw a new NoSuchFieldException object
         */
      }
    }
    throw new NoSuchFieldException(
        "Could not get value for field " + object.getClass().getName() + "." + name);
  }

  public static void setField(Object object, String name, Object value)
      throws NoSuchFieldException {
    if (object == null) {
      throw new IllegalArgumentException("Invalid null object argument");
    }
    for (Class<?> cls = object.getClass(); cls != null; cls = cls.getSuperclass()) {
      try {
        Field field = cls.getDeclaredField(name);
        field.setAccessible(true);
        field.set(object, value);
        return;
      } catch (Exception ex) {
        /*
         * in case of an exception, we will throw a new NoSuchFieldException object
         */
      }
    }
    throw new NoSuchFieldException(
        "Could set value for field " + object.getClass().getName() + "." + name);
  }
}
