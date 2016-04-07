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
