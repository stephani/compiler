package compiler;

public class TokenPair {

	private String token;
	private String lexeme;
	private int line_number;
	private int col_number;

	public TokenPair(String token, String lexeme, int line_number,
			int col_number) {
		this.token = token;
		this.lexeme = lexeme;
		this.line_number = line_number;
		this.col_number = col_number;
	}

	protected int getLine() {
		return line_number;
	}

	protected int getCol() {
		return col_number;
	}

	protected String getToken() {
		return token;
	}

	protected String getLexeme() {
		return lexeme;
	}
}