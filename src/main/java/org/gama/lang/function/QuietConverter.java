package org.gama.lang.function;

/**
* @author Guillaume Mary
*/
@FunctionalInterface
public interface QuietConverter<I, O> extends ThrowingConverter<I, O, RuntimeException> {
	
	abstract class NullAwareConverter<I, O> implements QuietConverter<I, O> {
		
		@Override
		public O convert(I input) {
			return input == null ? convertNull() : convertNotNull(input);
		}
		
		/**
		 * Called for returning a value when input is null.
		 * This implementation returns null
		 * 
		 * @return whatever needed
		 */
		protected O convertNull() {
			return null;
		}
		
		protected abstract O convertNotNull(I input);
	}
}
