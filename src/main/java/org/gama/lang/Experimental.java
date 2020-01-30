package org.gama.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marking annotation to be used on methods or classes that are in early stage or that, at least, should be enhanced 
 * 
 * @author Guillaume Mary
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Experimental {
	
	String[] todo() default {};
}
