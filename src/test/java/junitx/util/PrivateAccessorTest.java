package junitx.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PrivateAccessorTest {
	private String stringField;

	@Test(expected = IllegalArgumentException.class)
	public void testGetField_argumentNull() throws Exception {
		PrivateAccessor.getField(null, null);
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

	@Test
	public void testSetField() throws Exception {
		PrivateAccessor.setField(this, "stringField", "myValue");
		assertEquals("myValue", stringField);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvoke_argumentNull() throws Throwable {
		PrivateAccessor.invoke(null, null, null, null);
	}

	@Test
	public void testInvoke() throws Throwable {
		assertEquals("123 true", PrivateAccessor.invoke(this, "theMethod", new Class[] { Long.class, Boolean.class },
				new Object[] { Long.valueOf(123), Boolean.TRUE }));
	}

	@SuppressWarnings("unused")
	private String theMethod(Long argument1, Boolean argument2) {
		return String.format("%s %s", argument1, argument2);
	}
}
