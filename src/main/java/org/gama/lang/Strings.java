package org.gama.lang;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * @author Guillaume Mary
 */
public abstract class Strings {
	
	public static boolean isEmpty(CharSequence charSequence) {
		return charSequence == null || charSequence.length() == 0;
	}
	
	public static CharSequence preventEmpty(CharSequence charSequence, CharSequence replacement) {
		return isEmpty(charSequence) ? replacement : charSequence;
	}
	
	public static String capitalize(final CharSequence cs) {
		return (String) doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(CharSequence cs) {
				return Character.toUpperCase(cs.charAt(0)) + cs.subSequence(1, cs.length()).toString();
			}
		});
	}
	
	public static String uncapitalize(final CharSequence cs) {
		return (String) doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(CharSequence cs) {
				return Character.toLowerCase(cs.charAt(0)) + cs.subSequence(1, cs.length()).toString();
			}
		});
	}
	
	/**
	 * Concatenate count (positive) times parameter s.
	 * Optional Strings in prebuildStrings are used to speed concatenation for large count numbers if you already have
	 * large snippets of s pre-concatenated. For instance, you want 3456 times "a" and you already got constants with
	 * a*500, a*100, a*10, then this method will only cat 6*a*500, 4*a*100, 5*a*10 and 6*a. Instead of 3456 times "a".
	 *
	 * @param count expected repeatition of s
	 * @param s the String to be concatenated
	 * @param prebuiltStrings optional pre-concatenated "s" strings, <b>in descent size order</b>.
	 * @return s repeated count times
	 */
	public static StringBuilder repeat(int count, CharSequence s, String... prebuiltStrings) {
		StringBuilder result = new StringBuilder(count * s.length());
		repeat(result, count, s, prebuiltStrings);
		return result;
	}
	
	/**
	 * Concatenate count (positive) times parameter s.
	 * Optional Strings in prebuildStrings are used to speed concatenation for large count numbers if you already have
	 * large snippets of s pre-concatenated. For instance, you want 3456 times "a" and you already got constants with
	 * a*500, a*100, a*10, then this method will only cat 6*a*500, 4*a*100, 5*a*10 and 6*a. Instead of 3456 times "a".
	 *
	 * @param result destination of the concatenation
	 * @param count expected repeatition of s
	 * @param s the String to be concatenated
	 * @param prebuiltStrings optional pre-concatenated "s" strings, <b>in descent size order</b>.
	 * @return s repeated count times
	 */
	public static StringBuilder repeat(StringBuilder result, int count, CharSequence s, String... prebuiltStrings) {
		int snippetCount, remainer = count;
		for (String snippet : prebuiltStrings) {
			int snippetLength = snippet.length();
			snippetCount = remainer / snippetLength;
			for (int i = 0; i < snippetCount; i++) {
				result.append(snippet);
			}
			remainer = remainer % snippetLength;
		}
		for (int i = 0; i < remainer; i++) {
			result.append(s);
		}
		return result;
	}
		
	public static CharSequence head(@Nullable CharSequence cs, @Nonnegative int headSize) {
		return doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs) {
				return cs.subSequence(0, Math.min(cs.length(), preventNegative(headSize)));
			}
		});
	}
	
	public static CharSequence head(@Nullable String cs, @Nonnull String untilIncluded) {
		return doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs) {
				return cs.subSequence(0, Math.min(cs.length(), ((String) cs).indexOf(untilIncluded)+1));
			}
		});
	}
	
	public static CharSequence cutHead(@Nullable CharSequence cs, @Nonnegative int headSize) {
		return doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs) {
				return cs.subSequence(Math.min(preventNegative(headSize), cs.length()), cs.length());
			}
		});
	}
	
	public static CharSequence tail(@Nullable CharSequence cs, @Nonnegative int tailSize) {
		return doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs) {
				return cs.subSequence(Math.max(0, cs.length() - preventNegative(tailSize)), cs.length());
			}
		});
	}
	
	public static CharSequence cutTail(@Nullable CharSequence cs, @Nonnegative int tailSize) {
		return doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs) {
				return cs.subSequence(0, preventNegative(cs.length() - preventNegative(tailSize)));
			}
		});
	}
	
	/**
	 * @param i any integer
	 * @return 0 if i < 0
	 */
	private static int preventNegative(int i) {
		return i < 0 ? 0 : i;
	}
	
	private static CharSequence doWithDelegate(@Nullable CharSequence cs, INullOrEmptyDelegate emptyDelegate) {
		if (cs == null) {
			return emptyDelegate.onNull();
		} else if (cs.length() == 0) {
			return emptyDelegate.onEmpty();
		} else {
			return emptyDelegate.onNotNullNotEmpty(cs);
		}
	}
	
	/**
	 * Give a printable view of an object through method references of any of its properties. These will be concatenated to each other
	 * with comma (", ").
	 * Result of method references are printed by {@link StringBuilder#append(Object)} contract.
	 * 
	 * @param object any object (non null)
	 * @param printableProperties functions that give a properties to be concatenated
	 * @param <O> object type
	 * @return the concatenation of the results of functions invokation on the given object
	 */
	public static <O> String footPrint(O object, Function<O, ?> ... printableProperties) {
		StringAppender result = new StringAppender();
		for (Function<O, ?> printableProperty : printableProperties) {
			result.cat(printableProperty.apply(object), ", ");
		}
		return result.cutTail(2).toString();
	}
	
	private interface INullOrEmptyDelegate {
		CharSequence onNull();
		CharSequence onEmpty();
		CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs);
	}
	
	private static abstract class DefaultNullOrEmptyDelegate implements INullOrEmptyDelegate {
		@Override
		public CharSequence onNull() {
			return null;
		}
		
		@Override
		public CharSequence onEmpty() {
			return "";
		}
	}
	
	private Strings() {}
}
