package org.gama.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;

import org.gama.lang.Nullable;
import org.gama.lang.StringAppender;
import org.gama.lang.collection.Arrays;

/**
 * A class for custom Reflection API print. Mainly for compact package presentation.
 * 
 * @author Guillaume Mary
 */
public class MemberPrinter {
	
	/** A printer that shows the whole package name (full path) */
	public static final MemberPrinter FULL_PACKAGE_PRINTER = new MemberPrinter(Package::getName);
	
	/** A printer for compact package name presentation (first letter of each path) */
	public static final MemberPrinter FLATTEN_PACKAGE_PRINTER = new MemberPrinter(MemberPrinter::flattenPackage);
	
	/** A printer that won't print package name for "well known" package : java.util and java.lang */
	public static final MemberPrinter WELL_KNOWN_FLATTEN_PACKAGE_PRINTER = new MemberPrinter(new PackagePrinter() {
		
		private final Set<Package> WELL_KNOW_PACKAGES = Collections.unmodifiableSet(Arrays.asSet(
				Collection.class.getPackage(),
				String.class.getPackage()));
		
		@Override
		public String toString(Package aPackage) {
			if (WELL_KNOW_PACKAGES.contains(aPackage)) {
				// well-known packages are not printed
				return "";
			} else {
				return FLATTEN_PACKAGE_PRINTER.toString(aPackage);
			}
		}
	});
	
	/**
	 * Gives a compact view of a package name by keeping only first character of each path, separated by dots.
	 * 
	 * @param aPackage not null (not compatible with top level classes / primitive types)
	 * @return a String of the first character of package names
	 */
	private static String flattenPackage(Package aPackage) {
		StringAppender result = new StringAppender();
		StringTokenizer tokenizer = new StringTokenizer(aPackage.getName(), ".", false);
		while (tokenizer.hasMoreTokens()) {
			String packageName = tokenizer.nextToken();
			result.cat(packageName.charAt(0), ".");
		}
		result.cutTail(1);
		return result.toString();
	}
	
	private final PackagePrinter packagePrinter;
	
	public MemberPrinter(PackagePrinter packagePrinter) {
		this.packagePrinter = packagePrinter;
	}
	
	public String toString(Package aPackage) {
		// prevent top level class case
		return aPackage == null ? "" : packagePrinter.toString(aPackage);
	}
	
	public String toString(Class aClass) {
		Package classPackage = aClass.isArray() ? aClass.getComponentType().getPackage() : aClass.getPackage();
		String packageName = toString(classPackage);
		if (packageName.isEmpty()) {
			return aClass.getSimpleName();
		} else {
			return packageName + "." + Nullable.nullable(aClass.getEnclosingClass()).apply(c -> c.getSimpleName() + "$").orGet("") + aClass.getSimpleName();
		}
	}
	
	public String toString(Constructor constructor) {
		return new ClassAppender().cat(constructor.getDeclaringClass())
				.cat("(").ccat(constructor.getParameterTypes(), ", ").cat(")").toString();
	}
	
	public String toString(Method method) {
		return new ClassAppender().cat(method.getReturnType(), " ", method.getDeclaringClass(), ".", method.getName())
				.cat("(").ccat(method.getParameterTypes(), ", ").cat(")").toString();
	}
	
	public String toString(Field field) {
		return new ClassAppender().cat(field.getType(), " ", field.getDeclaringClass(), ".", field.getName()).toString();
	}
	
	
	@FunctionalInterface
	public interface PackagePrinter {
		
		/**
		 * Gives a representation of a package
		 * @param aPackage not null
		 * @return not null, an empty String in worst case
		 */
		String toString(Package aPackage);
	}
	
	private class ClassAppender extends StringAppender {
		@Override
		public StringAppender cat(Object o) {
			if (o instanceof Class) {
				return super.cat(MemberPrinter.this.toString((Class) o));
			} else {
				return super.cat(o);
			}
		}
	}
}
