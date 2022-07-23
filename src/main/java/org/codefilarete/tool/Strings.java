package org.codefilarete.tool;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

import org.codefilarete.tool.bean.Objects;

/**
 * @author Guillaume Mary
 */
public abstract class Strings {
	
	public static boolean isEmpty(CharSequence charSequence) {
		return charSequence == null || charSequence.length() == 0;
	}
	
	public static <C extends CharSequence> C preventEmpty(C charSequence, C replacement) {
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
	 * Concatenates count (positive) times parameter s.
	 * Optional Strings in prebuildStrings are used to speed concatenation for large count numbers if you already have
	 * large snippets of s pre-concatenated. For instance, you want 3456 times "a" and you already got constants with
	 * a*500, a*100, a*10, then this method will only cat 6*a*500, 4*a*100, 5*a*10 and 6*a. Instead of 3456 times "a".
	 *
	 * @param result destination of the concatenation
	 * @param count expected repetition of s
	 * @param s the {@link CharSequence} to be concatenated to result
	 * @param prebuiltStrings optional pre-concatenated "s" strings, <strong>in descent size order</strong>.
	 * @return result with s repeated count times appended
	 */
	public static StringBuilder repeat(StringBuilder result, int count, CharSequence s, CharSequence... prebuiltStrings) {
		result.ensureCapacity(result.length() + count * s.length());	// to avoid extra allocation cycles
		int snippetCount, remainer = count;
		for (CharSequence snippet : prebuiltStrings) {
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
				return cs.subSequence(0, Math.min(cs.length(), headSize));
			}
		});
	}
	
	public static CharSequence head(@Nullable String cs, @Nonnull String untilIncluded) {
		return doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs) {
				int index = ((String) cs).indexOf(untilIncluded);
				return cs.subSequence(0, Math.min(cs.length(), Objects.fallback(index, -1, 0)));
			}
		});
	}
	
	public static CharSequence cutHead(@Nullable CharSequence cs, @Nonnegative int headSize) {
		return doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs) {
				return cs.subSequence(Math.min(headSize, cs.length()), cs.length());
			}
		});
	}
	
	public static CharSequence tail(@Nullable CharSequence cs, @Nonnegative int tailSize) {
		return doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs) {
				return cs.subSequence(Math.max(0, cs.length() - tailSize), cs.length());
			}
		});
	}
	
	public static CharSequence cutTail(@Nullable CharSequence cs, @Nonnegative int tailSize) {
		return doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs) {
				return cs.subSequence(0, preventNegative(cs.length() - tailSize));
			}
		});
	}
	
	/**
	 * Cuts a {@link CharSequence} and appends 3 dots ("...") at the end if its length is strictly greater than length
	 * 
	 * @param cs any {@link CharSequence}
	 * @param length length at which given {@link CharSequence} must be cut and appended "..."
	 * @return length-firsts characters of given {@link CharSequence} appended with "...", therefore its size is length+3 
	 */
	public static CharSequence ellipsis(@Nullable CharSequence cs, @Nonnegative int length) {
		return doWithDelegate(cs, new DefaultNullOrEmptyDelegate() {
			@Override
			public CharSequence onNotNullNotEmpty(@Nonnull CharSequence cs) {
				if (cs.length() > length) {
					return cs.subSequence(0, length) + "...";
				} else {
					return cs;
				}
			}
		});
	}
	
	/**
	 * @param i any integer
	 * @return 0 if i < 0
	 */
	private static int preventNegative(int i) {
		return Math.max(i, 0);
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
	 * @param object any object (not null)
	 * @param printableProperties functions that give a properties to be concatenated
	 * @param <O> object type
	 * @return the concatenation of the results of functions invocation on the given object
	 */
	@SafeVarargs
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
