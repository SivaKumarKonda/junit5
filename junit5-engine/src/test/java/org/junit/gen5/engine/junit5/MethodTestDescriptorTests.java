/*
 * Copyright 2015 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.gen5.engine.junit5;

import static org.junit.gen5.api.Assertions.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.junit.gen5.api.Test;
import org.junit.gen5.engine.EngineDescriptor;
import org.junit.gen5.engine.junit5.descriptor.ClassTestDescriptor;
import org.junit.gen5.engine.junit5.descriptor.MethodTestDescriptor;
import org.junit.gen5.engine.junit5.stubs.TestEngineStub;

/**
 * Unit tests for {@link MethodTestDescriptor}.
 *
 * @author Sam Brannen
 * @author Stefan Bechtold
 * @since 5.0
 */
public class MethodTestDescriptorTests {

	private static final String TEST_METHOD_ID = MethodTestDescriptorTests.class.getName() + "#test()";
	private static final String TEST_METHOD_UID = TestEngineStub.TEST_ENGINE_DUMMY_ID + ":" + TEST_METHOD_ID;
	private static final String TEST_METHOD_STRING_BIGDECIMAL_ID = MethodTestDescriptorTests.class.getName()
			+ "#test(java.lang.String, java.math.BigDecimal)";
	private static final String TEST_METHOD_STRING_BIGDECIMAL_UID = TestEngineStub.TEST_ENGINE_DUMMY_ID + ":"
			+ TEST_METHOD_STRING_BIGDECIMAL_ID;
	private static final EngineDescriptor ENGINE_DESCRIPTOR = new EngineDescriptor(new TestEngineStub());

	@org.junit.Test
	public void constructFromMethod() throws Exception {
		Class<?> testClass = getClass();
		Method testMethod = testClass.getDeclaredMethod("test");
		ClassTestDescriptor parent = new ClassTestDescriptor(testClass, ENGINE_DESCRIPTOR);
		MethodTestDescriptor descriptor = new MethodTestDescriptor(testMethod, parent);

		assertEquals(TEST_METHOD_UID, descriptor.getUniqueId());
		assertEquals(testClass, descriptor.getParent().getTestClass());
		assertEquals(testMethod, descriptor.getTestMethod());
		assertEquals("test", descriptor.getDisplayName(), "display name:");
	}

	@org.junit.Test
	public void constructFromMethodWithCustomDisplayName() throws Exception {
		Class<?> testClass = getClass();
		Method testMethod = testClass.getDeclaredMethod("foo");
		ClassTestDescriptor parent = new ClassTestDescriptor(testClass, ENGINE_DESCRIPTOR);
		MethodTestDescriptor descriptor = new MethodTestDescriptor(testMethod, parent);

		assertEquals(testClass, descriptor.getParent().getTestClass());
		assertEquals(testMethod, descriptor.getTestMethod());
		assertEquals("custom test name", descriptor.getDisplayName(), "display name:");
	}

	@org.junit.Test
	public void constructFromMethodWithCustomDisplayNameInCustomTestAnnotation() throws Exception {
		Class<?> testClass = getClass();
		Method testMethod = testClass.getDeclaredMethod("customTestAnnotation");
		ClassTestDescriptor parent = new ClassTestDescriptor(testClass, ENGINE_DESCRIPTOR);
		MethodTestDescriptor descriptor = new MethodTestDescriptor(testMethod, parent);

		assertEquals(testClass, descriptor.getParent().getTestClass());
		assertEquals(testMethod, descriptor.getTestMethod());
		assertEquals("custom name", descriptor.getDisplayName(), "display name:");
	}

	@org.junit.Test
	public void constructFromMethodWithParameters() throws Exception {
		Class<?> testClass = getClass();
		Method testMethod = testClass.getDeclaredMethod("test", String.class, BigDecimal.class);
		ClassTestDescriptor parent = new ClassTestDescriptor(testClass, ENGINE_DESCRIPTOR);
		MethodTestDescriptor descriptor = new MethodTestDescriptor(testMethod, parent);

		assertEquals(TEST_METHOD_STRING_BIGDECIMAL_UID, descriptor.getUniqueId());
		assertEquals(testClass, descriptor.getParent().getTestClass());
		assertEquals(testMethod, descriptor.getTestMethod());
		assertEquals("test", descriptor.getDisplayName(), "display name:");
	}

	void test() {
	}

	void test(String txt, BigDecimal sum) {
	}

	@Test(name = "custom test name")
	void foo() {
	}

	@CustomTestAnnotation
	void customTestAnnotation() {
	}

	@Test(name = "custom name")
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	static @interface CustomTestAnnotation {
	}

}
