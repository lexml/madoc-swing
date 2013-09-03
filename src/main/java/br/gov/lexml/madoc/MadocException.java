package br.gov.lexml.madoc;

public class MadocException extends Exception {

	private static final long serialVersionUID = 1L;

	public MadocException() {
	}

	public MadocException(String message) {
		super(message);
	}

	public MadocException(Throwable cause) {
		super(cause);
	}

	public MadocException(String message, Throwable cause) {
		super(message, cause);
	}

}
