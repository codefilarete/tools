package org.gama.lang.function;

import java.util.HashMap;
import java.util.Map;

import org.gama.lang.function.Converter.NullAwareConverter;

/**
 * @author Guilllaume Mary
 */
public class ConverterRegistry {
	
	/**
	 * Instance based on valueOf() methods of default JDK classes like {@link Integer#valueOf(String)}
	 * Aimed at reading {@link java.util.Properties} values, for instance.
	 */
	public static final ConverterRegistry DEFAULT = new ConverterRegistry();
	
	static {
		DEFAULT.setConverter(Integer.class, new NullAwareConverter<Object, Integer>() {
			@Override
			public Integer convertNotNull(Object o) {
				return Integer.valueOf(o.toString());
			}
		});
		DEFAULT.setConverter(Long.class, new NullAwareConverter<Object, Long>() {
			@Override
			public Long convertNotNull(Object o) {
				return Long.valueOf(o.toString());
			}
		});
		DEFAULT.setConverter(Double.class, new NullAwareConverter<Object, Double>() {
			@Override
			public Double convertNotNull(Object o) {
				return Double.valueOf(o.toString());
			}
		});
		DEFAULT.setConverter(Float.class, new NullAwareConverter<Object, Float>() {
			@Override
			public Float convertNotNull(Object o) {
				return Float.valueOf(o.toString());
			}
		});
		DEFAULT.setConverter(Boolean.class, new NullAwareConverter<Object, Boolean>() {
			@Override
			public Boolean convertNotNull(Object o) {
				return Boolean.valueOf(o.toString());
			}
		});
		DEFAULT.setConverter(String.class, new NullAwareConverter<Object, String>() {
			@Override
			public String convertNotNull(Object o) {
				return (String) o;
			}
		});
	}
	
	private final Map<Class, Converter> converters = new HashMap<>();
	
	public ConverterRegistry() {
	}
	
	public <O> void setConverter(Class<O> clazz, Converter<Object, O> converter) {
		converters.put(clazz, converter);
	}
	
	public Integer asInteger(Object value) {
		return as(Integer.class, value);
	}
	
	public Long asLong(Object value) {
		return as(Long.class, value);
	}
	
	public Double asDouble(Object value) {
		return as(Double.class, value);
	}
	
	public Float asFloat(Object value) {
		return as(Float.class, value);
	}
	
	public Boolean asBoolean(Object value) {
		return as(Boolean.class, value);
	}
	
	public String asString(Object value) {
		return as(String.class, value);
	}
	
	protected <O> O as(Class<O> clazz, Object value) {
		return getConverter(clazz).convert(value);
	}
	
	public <O> Converter<Object, O> getConverter(Class<O> clazz) {
		return this.converters.get(clazz);
	}
}
