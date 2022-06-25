package org.codefilarete.tool.collection;

import org.codefilarete.tool.Duo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class KeepOrderMapTest {
	
	@Test
	void orderIsKept() {
		KeepOrderMap<String, String> testInstance = new KeepOrderMap<>();
		testInstance.put("b", "bbb");
		testInstance.put("a", "aaa");
		testInstance.put("c", "ccc");
		
		List<Duo<String, String>> result = testInstance.entrySet().stream().map(e -> new Duo<>(e.getKey(), e.getValue())).collect(Collectors.toList());
		assertThat(result).isEqualTo(Arrays.asList(
				new Duo<>("b", "bbb"),
				new Duo<>("a", "aaa"),
				new Duo<>("c", "ccc")
		));
	}
	
	@Test
	void removeAt() {
		KeepOrderMap<String, String> testInstance = new KeepOrderMap<>();
		testInstance.put("b", "bbb");
		testInstance.put("a", "aaa");
		testInstance.put("c", "ccc");
		
		testInstance.removeAt(1);
		assertThat(testInstance.get("a")).isNull();
		
		List<Duo<String, String>> result = testInstance.entrySet().stream().map(e -> new Duo<>(e.getKey(), e.getValue())).collect(Collectors.toList());
		assertThat(result).isEqualTo(Arrays.asList(
				new Duo<>("b", "bbb"),
				new Duo<>("c", "ccc")
		));
	}
	
	@Test
	void getAt() {
		KeepOrderMap<String, String> testInstance = new KeepOrderMap<>();
		testInstance.put("b", "bbb");
		testInstance.put("a", "aaa");
		testInstance.put("c", "ccc");
		
		assertThat(testInstance.getAt(1).getKey()).isEqualTo("a");
		assertThat(testInstance.getAt(1).getValue()).isEqualTo("aaa");
	}
}