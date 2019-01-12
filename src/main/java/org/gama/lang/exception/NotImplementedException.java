package org.gama.lang.exception;

/**
 * An exception aimed at being thrown for not implemented cases. For example :
 * <ul>
 *     <li>in the {@code default} statement of a swith/case that implements all cases of an enumeration: this will prevent from a non-evolved
 *     code on enumeration addition</li>
 *     <li>in a if/instanceof code block: will prevent from a new type that is not taken into account by the "legacy" code</li>
 * </ul>
 * 
 * Implemented as a specialization of {@link UnsupportedOperationException} to provide more semantic to it.
 * 
 * @author Guillaume Mary
 */
public class NotImplementedException extends UnsupportedOperationException {
	
	/* No default constructor to encourage detailed reason */
	
	public NotImplementedException(String message) {
		super(message);
	}
	
	public NotImplementedException(Enum notImplementedValue) {
		super("Not implemented case : " + notImplementedValue);
	}
	
	public NotImplementedException(Class notImplementedType) {
		super("Not implemented case : " + notImplementedType);
	}
	
	public NotImplementedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public NotImplementedException(Throwable cause) {
		super(cause);
	}
}
