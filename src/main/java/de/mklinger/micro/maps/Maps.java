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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Utility class for Maps.
 *
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class Maps {
	private static final int MIN_INITIAL_CAPACITY = 4;

	/** No instantiation */
	private Maps() {}

	/**
	 * Returns a HashMap with a capacity that is sufficient to keep the map
	 * from being resized as long as it grows no larger than expectedSize
	 * with the default load factor (0.75).
	 */
	public static <K, V> HashMap<K, V> newHashMap(final int expectedSize) {
		// See code in java.util.HashSet.HashSet(Collection<? extends E>)
		return new HashMap<>(Math.max((int) (expectedSize / .75f) + 1, MIN_INITIAL_CAPACITY));
	}

	/**
	 * Returns a LinkedHashMap with a capacity that is sufficient to keep the map
	 * from being resized as long as it grows no larger than expectedSize with the
	 * default load factor (0.75).
	 */
	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final int expectedSize) {
		// See code in java.util.HashSet.HashSet(Collection<? extends E>)
		return new LinkedHashMap<>(Math.max((int) (expectedSize / .75f) + 1, MIN_INITIAL_CAPACITY));
	}

	/**
	 * Create an immutable map containing the same entries as the given map.
	 * @param original The original map
	 * @return A new immutable map
	 */
	public static <K, V> Map<K, V> newImmutableMap(final Map<K, V> original) {
		if (original == null || original.isEmpty()) {
			return Collections.emptyMap();
		}
		if (original.size() == 1) {
			final Entry<K, V> entry = original.entrySet().iterator().next();
			return Collections.singletonMap(entry.getKey(), entry.getValue());
		}
		return Collections.unmodifiableMap(new HashMap<>(original));
	}

	/**
	 * Create a new HashMap with the given keys and values.
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> HashMap<K, V> newHashMap(final K key1, final V value1, final Object... others) {
		if (others != null && others.length > 0) {
			if (others.length % 2 != 0) {
				throw new IllegalArgumentException();
			}
			final HashMap<K, V> m = newHashMap(others.length / 2 + 1);
			m.put(key1, value1);
			for (int i = 0; i < others.length; i+=2) {
				m.put((K)others[i], (V)others[i + 1]);
			}
			return m;
		} else {
			final HashMap<K, V> m = newHashMap(1);
			m.put(key1, value1);
			return m;
		}
	}

	/**
	 * Create a new map that contains all entries from the given original map and
	 * the additional keys and values. Additional key/value pairs will overwrite
	 * original key/value pairs.
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> HashMap<K, V> extend(final Map<K, V> original, final K key1, final V value1, final Object... others) {
		Objects.requireNonNull(original);
		final HashMap<K, V> m = newHashMap(original.size() + others.length / 2 + 1);
		m.putAll(original);
		m.put(key1, value1);
		if (others != null && others.length > 0) {
			if (others.length % 2 != 0) {
				throw new IllegalArgumentException();
			}
			for (int i = 0; i < others.length; i+=2) {
				m.put((K)others[i], (V)others[i + 1]);
			}
		}
		return m;
	}

	/**
	 * Create a new TreeMap with the given keys and values.
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> TreeMap<K, V> newTreeMap(final K key1, final V value1, final Object... others) {
		final TreeMap<K, V> m = new TreeMap<>();
		m.put(key1, value1);
		if (others != null && others.length > 0) {
			if (others.length % 2 != 0) {
				throw new IllegalArgumentException();
			}
			for (int i = 0; i < others.length; i+=2) {
				m.put((K)others[i], (V)others[i + 1]);
			}
		}
		return m;
	}
}
