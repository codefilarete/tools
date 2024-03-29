package org.codefilarete.tool.exception;

import java.util.ArrayList;
import java.util.List;

import org.codefilarete.tool.StringAppender;

/**
 * @author Guillaume Mary
 */
public class MultiCauseException extends RuntimeException {
	
	private final List<Throwable> causes = new ArrayList<>();
	
	@Override
	public synchronized Throwable initCause(Throwable cause) {
		addCause(cause);
		return this;
	}
	
	public void addCause(Throwable t) {
		this.causes.add(t);
	}
	
	public List<? extends Throwable> getCauses() {
		return causes;
	}
	
	public void throwIfNotEmpty() {
		if (!this.causes.isEmpty()) {
			if (this.causes.size() == 1) {
				throw Exceptions.asRuntimeException(this.causes.get(0));
			} else {
				throw this;
			}
		}
	}
	
	@Override
	public String getMessage() {
		StringAppender message = new StringAppender(1024);
		for (Throwable cause : causes) {
			message.cat(cause.getMessage(), ", ");
		}
		message.cutTail(2);
		return "Multi cause exception : " + message;
	}
}
