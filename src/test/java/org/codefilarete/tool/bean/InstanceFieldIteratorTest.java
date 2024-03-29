package org.codefilarete.tool.bean;

import java.lang.reflect.Field;
import java.util.List;

import org.codefilarete.tool.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
public class InstanceFieldIteratorTest {
	
	// These fields are noise for test case 
	private static Object staticField1;
	protected static Object staticField2;
	public static Object staticField3;
	static Object staticField4;

	private String field1;
	protected String field2;
	public String field3;
	String field4;
	
	@Test
	public void testGetElements() throws Exception {
		InstanceFieldIterator testInstance = new InstanceFieldIterator(getClass());
		List<Field> expectedResult = Arrays.asList(
				getClass().getDeclaredField("field1"),
				getClass().getDeclaredField("field2"),
				getClass().getDeclaredField("field3"),
				getClass().getDeclaredField("field4")
		);
		assertThat(Arrays.asList(testInstance.getElements(getClass()))).isEqualTo(expectedResult);
	}
	
}