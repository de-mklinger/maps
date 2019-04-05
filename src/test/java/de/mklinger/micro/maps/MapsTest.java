/*
 * Copyright mklinger GmbH - https://www.mklinger.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mklinger.micro.maps;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class MapsTest {
	@Test
	public void initialCapacityTest() {
		final int size = 100;
		final HashMap<String, String> hashMap = Maps.newHashMap(100);
		final int initialCapacity = getCapacity(hashMap);

		for (int i = 0; i < size; i++) {
			hashMap.put("key-" + i, "value-" + i);
			assertThat(getCapacity(hashMap), is(initialCapacity));
		}
	}

	private int getCapacity(final HashMap<?, ?> hashMap) {
		try {
			final Method method = HashMap.class.getDeclaredMethod("capacity");
			method.setAccessible(true);
			return (int) method.invoke(hashMap);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void newHashMapTest() {
		final HashMap<String, String> m = Maps.newHashMap("x", "y", "a", "b");
		assertThat(m.size(), is(2));
		assertThat(m.get("x"), is("y"));
		assertThat(m.get("a"), is("b"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void newHashMapErrorTest() {
		Maps.newHashMap("x", "y", "a");
	}

	@Test
	public void newHashMapTest2() {
		final HashMap<String, Integer> m = Maps.newHashMap("x", 1, "a", 2);
		assertThat(m.size(), is(2));
		assertThat(m.get("x"), is(1));
		assertThat(m.get("a"), is(2));
	}

	@Test
	public void newHashMapTest3() {
		final HashMap<String, Integer> m = Maps.newHashMap("x", 1);
		assertThat(m.size(), is(1));
		assertThat(m.get("x"), is(1));
	}

	@Test
	public void extendTest() {
		final HashMap<String, String> old = Maps.newHashMap("old1", "old1", "old2", "old2", "x", "y");
		final HashMap<String, String> m = Maps.extend(old, "x", "y", "a", "b");
		assertThat(m.size(), is(4));
		assertThat(m.get("old1"), is("old1"));
		assertThat(m.get("old2"), is("old2"));
		assertThat(m.get("x"), is("y"));
		assertThat(m.get("a"), is("b"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void extendErrorTest() {
		Maps.extend(Collections.emptyMap(), "x", "y", "a");
	}

	@Test
	public void newTreeMapTest() {
		final TreeMap<String, String> m = Maps.newTreeMap("x", "y", "a", "b");
		assertThat(m.size(), is(2));
		assertThat(m.get("x"), is("y"));
		assertThat(m.get("a"), is("b"));
		assertThat(new ArrayList<>(m.keySet()), equalTo(asList("a", "x")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void newTreeMapErrorTest() {
		Maps.newTreeMap("x", "y", "a");
	}

	@Test
	public void newImmutableMapTest() {
		newImmutableMapTest(100);
	}

	@Test
	public void newImmutableMapTestEmpty() {
		newImmutableMapTest(0);
	}

	@Test
	public void newImmutableMapSingleton() {
		newImmutableMapTest(1);
	}

	private void newImmutableMapTest(final int size) {
		final Map<String, String> src = new HashMap<>();
		for (int i = 0; i < size; i++) {
			src.put("key-" + i, "value-" + i);
		}
		final Map<String, String> immutable = Maps.newImmutableMap(src);
		assertEquals(src, immutable);

		assertImmutable(immutable);

		if (size > 1) {
			assertStrictImmutable(immutable);
		}
	}

	private void assertImmutable(final Map<String, String> immutable) {
		assertException(UnsupportedOperationException.class, () -> immutable.put("bla", "blub"));
		assertException(UnsupportedOperationException.class, () -> immutable.putAll(Collections.singletonMap("bla", "blub")));
	}

	private void assertStrictImmutable(final Map<String, String> immutable) {
		assertException(UnsupportedOperationException.class, () -> immutable.keySet().iterator().remove());
		assertException(UnsupportedOperationException.class, () -> immutable.values().iterator().remove());
		assertException(UnsupportedOperationException.class, () -> immutable.entrySet().iterator().remove());
		assertException(UnsupportedOperationException.class, () -> immutable.clear());
		assertException(UnsupportedOperationException.class, () -> immutable.remove("bla"));
	}

	private void assertException(final Class<? extends Throwable> exceptionType, final Runnable r) {
		try {
			r.run();
			fail("Expected exception of type " + exceptionType.getName() + ", but no exception was thrown");
		} catch (final Throwable e) {
			if (!exceptionType.isAssignableFrom(e.getClass())) {
				fail("Expected exception of type " + exceptionType.getName() + ", but was " + e.getClass().getName());
			}
		}
	}
}
