package org.codefilarete.tool.collection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class CaseInsensitiveSetTest {

    @Test
    public void addString_stringIsFoundWithoutCaseSensitivity() {
        CaseInsensitiveSet testInstance = new CaseInsensitiveSet();
    
        testInstance.add("Hello");
    
        assertThat(testInstance.contains("Hello")).isTrue();
        assertThat(testInstance.contains("HELLO")).isTrue();
        assertThat(testInstance.contains("hello")).isTrue();
        assertThat(testInstance.size()).isEqualTo(1);
    }

    // edge case
    @Test
    public void addNull_throwsException() {
        CaseInsensitiveSet testInstance = new CaseInsensitiveSet();
        assertThatCode(() -> testInstance.add(null))
                .isInstanceOf(NullPointerException.class);
    }
}