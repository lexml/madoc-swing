package br.gov.lexml.madoc.schema.parser;

import br.gov.lexml.madoc.MadocException;

public class ParseException extends MadocException {

	private static final long serialVersionUID = -6392262661142575607L;

	public ParseException() {
	}

	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
