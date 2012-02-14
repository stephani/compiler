package compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.LinkedList;

//main method should be dispatcher, controlling the various scanner functions.

//dispatcher will call on fsa functions as well as the skip_white_space function
//also will keep track of file pointer location
//@input: file to scan
//output: text file that contains tokens from scanner

public class Scanner {

	
	private StringBuffer inFileBuffer;
	private int filePointer;
	private int line;
	private int column;
	private LinkedList<TokenPair> tokenQueue;
	String token = "";
	StringBuffer lexeme;

	public Scanner() {
		// Postcondition: StringBuffer to contain entire buffer is created and
		// is empty
		// Postcondition: filePointer is at value/position 0
		// Postcondition: line and column are at value/position 1
		// Postcondition: LinkedList of TokenPair objects is created and is
		// empty

		inFileBuffer = new StringBuffer();
		filePointer = 0;
		line = 1;
		column = 1;

		tokenQueue = new LinkedList<TokenPair>();
	}

	protected int openFile(String inFile) {

		/*
		 * Read file into StringBuffer Returns 0 if successful, 1 if fails
		 * 
		 * Precondition: StringBuffer is empty and available to store inFile
		 * Postcondition: StringBuffer contains entire inFile
		 */

		BufferedReader reader = null;
		int returnStatus = 0; // 0 success,1 fail

		try {
			reader = new BufferedReader(new FileReader(new File(inFile)));
			int inChar;

			while ((inChar = reader.read()) != -1) {
				inFileBuffer.append((char) inChar);
			}

		} catch (FileNotFoundException e) {
			System.out.println("Error msg: Input file not found");
			returnStatus = 1; // fail
		} catch (IOException e) {
			System.out.println("Error msg:Exception reading file");
			returnStatus = 1; // fail
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				System.out.println("Error msg");
				returnStatus = 1; // fail
			}
		}

		return returnStatus;
	}
	//method to write to outputFile
	//testing with printQueue right now
	//will use printTokenPair in here though
	protected void writeFile(String o, LinkedList<TokenPair> tq) throws IOException {
		BufferedWriter out = null;
		try{
			
			out = new BufferedWriter(new FileWriter(o));
			while (!tq.isEmpty()) {
				TokenPair tp = getTokenPair();
				String os = String.format("%25s%10d%10d%20s",tp.getToken(),tp.getLine(),tp.getCol(),tp.getLexeme()+"\n");
				out.write(os);
			}
		}catch (Exception e) {
			System.out.println("error writing to file");
		}finally {
		    	out.close();
		}		
	}

	private void addTokenToQueue(TokenPair tp) {
		/*
		 * precondition: TokenPair object has been created postcondition:
		 * TokenPair object has been added to the Token Object Queue
		 */
		//System.out.println(tp.getLexeme());
		tokenQueue.add(tp);
	}

	/*// need to change this to be able to print out each TokenPair
	//maybe printQueue as separate method??
	protected void printTokenPair(TokenPair tp) {
		//System.out.println(token+"    "+line+"    "+ column +"    "+lexeme);
		System.out.format("%25s%10d%10d%20s", tp.getToken(), tp.getLine(), tp.getCol(), tp.getLexeme());
		System.out.println();
	}
	
	protected void printQueue(LinkedList<TokenPair> tq) {
		while(!tq.isEmpty()) {			
			printTokenPair(getTokenPair());		
		}		
	}
	*/
	protected LinkedList<TokenPair> getQueue() {
		return tokenQueue;
	}

	protected TokenPair getTokenPair() {
		/*
		 * precondition: tokenQueue exists postcondition: first TokenPair has
		 * been removed from tokenQueue
		 */
		if (tokenQueue.size() == 0) {
			return null;
		}
		return tokenQueue.removeFirst();
	}

	// method to scan thru
	// will skip whitespaces, skip comments, call on appropriate FSAs
	protected void dispatcher() {
		/*
		 * Dispatcher starts here 
		 * precondition: Entire program file has been added to the filebuffer 
		 * precondition: TokenQueue has been created,is empty and is ready to add TokenPair objects 
		 * postcondition: FileBuffer has been completely read 
		 * postcondition: all tokens from file have been added to the queue
		 */
		System.out.println("starting dispatcher");
		
		while (filePointer < inFileBuffer.length()) {
			char inChar = inFileBuffer.charAt(filePointer);
			int prevPointer = filePointer;

			System.out.println("inChar: "+inChar);
			if ((int) inChar > 127) {
				TokenPair tp = new TokenPair("MP_ERROR", "Not ASCII", line,
						column);
				addTokenToQueue(tp);
				filePointer++;
				continue;
			}

			if (inChar == '\n') {
				// whitespace, so doesn't add to TokenQueue
				line++;
				column = 1; // new line, start at beginning
				filePointer++;
				continue;
			}

			if (inChar == '\r') {
				column = 1; // new line, start at beginning
				filePointer++;
				continue;
			}
			// start fsas and other methods
			if (inChar == '{') {
				skipComment();
			} else if (isLetter(inChar) || inChar == '_') {
				fsaLetter();
			} else if (isDigit(inChar)) {
				fsaDigit();
			} else if (inChar == '\'') { // if it starts with a single quote
				fsaStringLit();
			} else if (isWhiteSpace(inChar)) {
				// make skipwhitespace method??
				filePointer++;
				continue;
			} else if (inChar == '>') {
				fsaGThan();
			} else if (inChar == '<') {
				fsaLThan();
			} else if (inChar == ':') {
				fsaColon();
			} else if (inChar == '.') {
				TokenPair tp = new TokenPair("MP_PERIOD", ".", line, column);
				addTokenToQueue(tp);
				filePointer++;
			} else if (inChar == ',') {
				TokenPair tp = new TokenPair("MP_COMMA", ",", line, column);
				addTokenToQueue(tp);
				filePointer++;
			} else if (inChar == ';') {
				TokenPair tp = new TokenPair("MP_SCOLON", ";", line, column);
				addTokenToQueue(tp);
				filePointer++;
			} else if (inChar == '(') {
				TokenPair tp = new TokenPair("MP_LPAREN", "(", line, column);
				addTokenToQueue(tp);
				filePointer++;
			} else if (inChar == ')') {
				TokenPair tp = new TokenPair("MP_RPAREN", ")", line, column);
				addTokenToQueue(tp);
				filePointer++;
			} else if (inChar == '=') {
				TokenPair tp = new TokenPair("MP_EQUAL", "=", line, column);
				addTokenToQueue(tp);
				filePointer++;
			} else if (inChar == '+') {
				TokenPair tp = new TokenPair("MP_PLUS", "+", line, column);
				addTokenToQueue(tp);
				filePointer++;
			} else if (inChar == '-') {
				TokenPair tp = new TokenPair("MP_MINUS", "-", line, column);
				addTokenToQueue(tp);
				filePointer++;
			} else if (inChar == '*') {
				TokenPair tp = new TokenPair("MP_TIMES", "*", line, column);
				addTokenToQueue(tp);
				filePointer++;
			} else { // if it's anything else, store it as lexeme and type it as
						// MP_ERROR
				TokenPair tp = new TokenPair("MP_ERROR", inChar + "", line,
						column);
				addTokenToQueue(tp);
				filePointer++;
			}
			column += (filePointer - prevPointer);

		}// end while
	}// end dispatcher()

	// FSA methods
	private void fsaLetter() { // Derek

		StringBuffer lexeme = new StringBuffer();
		String str;
		boolean immediateUnderscore = false;
		boolean finished = false;
		char c = inFileBuffer.charAt(filePointer);
		lexeme.append(c);

		if (c == '_') {
			immediateUnderscore = true;
		}

		while (!finished) {
			filePointer++;
			c = inFileBuffer.charAt(filePointer);
			if (isWhiteSpace(c) || c ==';') {
				// done, and break -- return pointer pointing at whitespace char
				if (immediateUnderscore) {
					System.out.println("Error: Ending an id in an underscore. Debug fsaletter()");
					finished = true;
					break;
				}
				finished = true;
				break;
			} else { // not whitespace
				if (isLetter(c)) {
					lexeme.append(c);
					immediateUnderscore = false;
					continue;
				} else if (isDigit(c)) {
					lexeme.append(c);
					immediateUnderscore = false;
					continue;
				} else if (c == '_' && !immediateUnderscore) {
					lexeme.append(c);
					immediateUnderscore = true;
					continue;
				} else if (c == '_') { // if c is an underscore, and there was
										// an underscore before
					System.out.println("Error: Two underscores in input File");
					finished = true;
					break;
				} else {
				//end of token
					break;
				}
			}
		}
		// done, and our identifier string should be set up!
		// now to categorize it!!

		str = lexeme.toString();

		if (str.equals("and")) {
			TokenPair and = new TokenPair("MP_AND", "and", line, column);
			addTokenToQueue(and);
		} else if (str.equals("begin")) {
			TokenPair begin = new TokenPair("MP_BEGIN", "begin", line, column);
			addTokenToQueue(begin);
		} else if (str.equals("div")) {
			TokenPair div = new TokenPair("MP_DIV", "div", line, column);
			addTokenToQueue(div);
		} else if (str.equals("do")) {
			TokenPair doo = new TokenPair("MP_DO", "do", line, column);
			addTokenToQueue(doo);
		} else if (str.equals("downto")) {
			TokenPair downto = new TokenPair("MP_DOWNTO", "downto", line,
					column);
			addTokenToQueue(downto);
		} else if (str.equals("else")) {
			TokenPair elsee = new TokenPair("MP_ELSE", "else", line, column);
			addTokenToQueue(elsee);
		} else if (str.equals("end")) {
			TokenPair end = new TokenPair("MP_END", "end", line, column);
			addTokenToQueue(end);
		} else if (str.equals("fixed")) {
			TokenPair fixed = new TokenPair("MP_FIXED", "fixed", line, column);
			addTokenToQueue(fixed);
		} else if (str.equals("float")) {
			TokenPair floatt = new TokenPair("MP_FLOAT", "float", line, column);
			addTokenToQueue(floatt);
		} else if (str.equals("for")) {
			TokenPair forr = new TokenPair("MP_FOR", "for", line, column);
			addTokenToQueue(forr);
		} else if (str.equals("function")) {
			TokenPair function = new TokenPair("MP_FUNCTION", "function", line,
					column);
			addTokenToQueue(function);
		} else if (str.equals("if")) {
			TokenPair iff = new TokenPair("MP_IF", "if", line, column);
			addTokenToQueue(iff);
		} else if (str.equals("integer")) {
			TokenPair integer = new TokenPair("MP_INTEGER", "integer", line,
					column);
			addTokenToQueue(integer);
		} else if (str.equals("mod")) {
			TokenPair mod = new TokenPair("MP_MOD", "mod", line, column);
			addTokenToQueue(mod);
		} else if (str.equals("not")) {
			TokenPair not = new TokenPair("MP_NOT", "not", line, column);
			addTokenToQueue(not);
		} else if (str.equals("or")) {
			TokenPair or = new TokenPair("MP_OR", "or", line, column);
			addTokenToQueue(or);
		} else if (str.equals("procedure")) {
			TokenPair procedure = new TokenPair("MP_PROCEDURE", "procedure",
					line, column);
			addTokenToQueue(procedure);
		} else if (str.equals("program")) {
			TokenPair program = new TokenPair("MP_PROGRAM", "program", line,
					column);
			addTokenToQueue(program);
		} else if (str.equals("read")) {
			TokenPair read = new TokenPair("MP_READ", "read", line, column);
			addTokenToQueue(read);
		} else if (str.equals("repeat")) {
			TokenPair repeat = new TokenPair("MP_REPEAT", "repeat", line,
					column);
			addTokenToQueue(repeat);
		} else if (str.equals("then")) {
			TokenPair then = new TokenPair("MP_THEN", "then", line, column);
			addTokenToQueue(then);
		} else if (str.equals("to")) {
			TokenPair to = new TokenPair("MP_TO", "to", line, column);
			addTokenToQueue(to);
		} else if (str.equals("until")) {
			TokenPair until = new TokenPair("MP_UNTIL", "until", line, column);
			addTokenToQueue(until);
		} else if (str.equals("var")) {
			TokenPair var = new TokenPair("MP_VAR", "var", line, column);
			addTokenToQueue(var);
		} else if (str.equals("while")) {
			TokenPair whilee = new TokenPair("MP_WHILE", "while", line, column);
			addTokenToQueue(whilee);
		} else if (str.equals("write")) {
			TokenPair write = new TokenPair("MP_WRITE", "write", line, column);
			addTokenToQueue(write);
		} else {
			TokenPair id = new TokenPair("MP_IDENTIFIER", str, line, column);
			addTokenToQueue(id);
		}

	}

	private void fsaDigit() { // Rob
		// Digit := integer (MD_INTEGER), fixed (MD_FIXED), floatT (MD_FLOAT)
		StringBuffer lexeme = new StringBuffer();
		boolean finished = false;
		int state = 0;
		// 0 starting state
		// 1 integer *accept state*
		// 2 fixed *accept state*
		// 3 added exponent literal(e,E)
		// 4 float *accept state*
		while (!finished) {
			char c = inFileBuffer.charAt(filePointer);
			filePointer++;

			switch (state) {
			case 0: // 0 starting state
				if (c >= '0' && c <= '9') {
					lexeme.append(c);
					state = 1;
					break;
				} else {
					//System.out.println("Debug: fsaDigit: non-digit character found 0");
					finished = true;
					break;
				}
			case 1: // 1 integer *accept state*
				if (c >= '0' && c <= '9') { // processing integer
					lexeme.append(c);
					state = 1;
					break;
				} else if (c == '.') {
					lexeme.append(c);
					state = 2;
					break;
				} else if (c == 'e' || c == 'E') { // exponent character found
					lexeme.append(c);
					state = 3;
					break;
				} else { // any other character
					//System.out.println("Debug: fsaDigit: non-digit character found 1");
					finished = true;
					break;
				}
			case 2: // 2 fixed *accept state*
				if (c >= '0' && c <= '9') { // processing integer
					lexeme.append(c);
					state = 2;
					break;
				} else if (c == 'e' || c == 'E') { // exponent character found
					lexeme.append(c);
					state = 3;
					break;
				} else { // any other character
					//System.out.println("Debug: fsaDigit: non-digit character found 2");
					finished = true;
					break;
				}
			case 3: // 3 added exponent literal(e,E)
				if ((c >= '1' && c <= '9') || c == '+' || c == '-') { // processing
																	// integer
					lexeme.append(c);
					state = 4;
					break;
				} else { // any other character
					//System.out.println("Debug: fsaDigit: non-digit character found 3");
					finished = true;
					break;
				}
			case 4: // 4 float *accept state*
				if (c >= '0' && c <= '9') { // processing integer
					lexeme.append(c);
					state = 4;
					break;
				} else { // any other character
					//System.out.println("Debug: fsaDigit: non-digit character found "+c + " ");
					finished = true;
					break;
				}

			}// end switch
			//System.out.println("char "+c);
		}// end while
		
		if ((state == 0) || (state == 3)) { // bad data
			TokenPair tp = new TokenPair("MP_ERROR", lexeme.toString(), line,
					column);
			addTokenToQueue(tp);
		} else if (state == 1) { // accept states
			TokenPair tp = new TokenPair("MP_INTEGER", lexeme.toString(), line,
					column);
			addTokenToQueue(tp);
		} else if (state == 2) { // accept states
			TokenPair tp = new TokenPair("MP_FIXED", lexeme.toString(), line,
					column);
			addTokenToQueue(tp);
		} else if (state == 4) { // accept states
			TokenPair tp = new TokenPair("MP_FLOAT", lexeme.toString(), line,
					column);
			addTokenToQueue(tp);
		}

	}

	private void fsaStringLit() { // Stephani

		StringBuffer lexeme = new StringBuffer();
		boolean finished = false;
		int state = 0;
		// 0 start
		// 1 odd number of apostrophe, any other char except EOL
		// 2 even number of apostrophes *accept state*
		while (!finished) {
			char c = inFileBuffer.charAt(filePointer);
			filePointer++;
			switch (state) {
			case 0: // starting state
				if (c == '\'') {
					lexeme.append(c);
					state = 1;
				} else {
					System.out.println("Debug: fsaStringLit: unexpected start character");
					moveBackChar();
					finished = true;
				}
				break;
			case 1: // odd number of apostrophe+any other char except EOL
				if (c == '\'') { // even number of apostrophe
					lexeme.append(c);
					state = 2;
				} else if ((int) c == 10 || (int) c == 12) { // EOL character
					System.out.println("Debug: fsaStringLit: new line found in string");
					moveBackChar();
					finished = true;
				} else { // any other character
					lexeme.append(c);
					state = 1; // loop until ending apostrophe found
				}
				break;
			case 2:  //accept state
				if (c == '\'') {
					lexeme.append(c);
					state=1;
				}
				else if (Character.isWhitespace(c)){
					//moveBackChar();
					finished=true;
				}
				else {//any other character
					lexeme.append(c);
					state=1;
				}
				break;
			case 3: 
				if (c == '\u00a0') {
					System.out.println("weird whitespace char");
					finished = true;
				}
			}// end switch

		}// end while

		if (state == 1 || state == 0) { // will it ever get here...yes, but it
										// will fail...so Error??
			TokenPair tp = new TokenPair("MP_ERROR", lexeme.toString(), line,
					column);
			addTokenToQueue(tp);
		} else if (state == 2) { // accept state
			TokenPair tp = new TokenPair("MP_STRING_LIT", lexeme.toString(),
					line, column);
			addTokenToQueue(tp);
		}

	}// end fsaStringLit()

	private void fsaLThan() {
		// < (MP_LTHAN) or <> (MP_NEQUAL) or <= (MP_LEQUAL)

		StringBuffer lexeme = new StringBuffer();
		lexeme.append('<');

		filePointer++;
		char c = inFileBuffer.charAt(filePointer);
		if (c == '=') {
			// there is an '=' following the <
			lexeme.append('=');
			String toReturn = lexeme.toString();
			TokenPair tp = new TokenPair("MP_LEQUAL", toReturn, line, column);
			addTokenToQueue(tp);
			filePointer++;
		} else if (c == '>') {
			// there is a '>' following the <
			lexeme.append('>');
			String toReturn = lexeme.toString();
			TokenPair tp = new TokenPair("MP_NEQUAL", toReturn, line, column);
			addTokenToQueue(tp);
			filePointer++;
		} else {
			// then there is whitespace, or an error
			//moveBackChar();
			String toReturn = lexeme.toString();
			TokenPair tp = new TokenPair("MP_LTHAN", toReturn, line, column);
			addTokenToQueue(tp);
		}
	}// end fsaLThan()

	private void fsaGThan() { // Derek
		// > (MP_GTHAN) or >= (MP_GEQUAL)

		StringBuffer lexeme = new StringBuffer();
		lexeme.append('>');

		filePointer++;
		char c = inFileBuffer.charAt(filePointer);
		if (c == '=') {
			// there is an '=' following the >
			lexeme.append('=');
			String toReturn = lexeme.toString();
			TokenPair tp = new TokenPair("MP_GEQUAL", toReturn, line, column);
			addTokenToQueue(tp);
			filePointer++;
		} else {
			// then there is whitespace, or an error
			String toReturn = lexeme.toString();
			TokenPair tp = new TokenPair("MP_GTHAN", toReturn, line, column);
			addTokenToQueue(tp);
		}
	}

	private void fsaColon() { // Stephani

		StringBuffer lexeme = new StringBuffer();
		boolean FsaFinish = false;
		// while !done
		int state = 0;
		// 0 start
		// 1 colon *accept*
		// 2 assign *accept*
		while (!FsaFinish) {

			char c = inFileBuffer.charAt(filePointer);
			filePointer++;

			switch (state) {
			case 0:
				if (c == ':') {
					lexeme.append(c);
					state = 1;
				} else {
					System.out.println("Debug: fsaColon(): unexpected start character");
					moveBackChar();
					FsaFinish = true;
				}
				break;

			case 1:
				if (c == '=') {
					lexeme.append(c);
					state = 2;
				} else {
					moveBackChar();
				}
				FsaFinish = true;
				break;

			}// end switch/case
		}// end while

		if (state == 1) {
			TokenPair tp = new TokenPair("MP_COLON", ":", line, column);
			addTokenToQueue(tp);
		} else if (state == 2) {
			TokenPair tp = new TokenPair("MP_ASSIGN", ":=", line, column);
			addTokenToQueue(tp);
		}
	}

	private void skipComment() {
		/*
		 * precondition: filePointer is at starting comment char
		 * 
		 * postcondition: filePointer is at first char after the comment
		 */
		boolean commentFinished = false;

		filePointer++;
		column++;
		while (!commentFinished && !(filePointer >= inFileBuffer.length())) {
			char inChar = inFileBuffer.charAt(filePointer);
			//System.out.println("char: "+inChar);
			if (inChar == '\r') {
				if ((filePointer) == inFileBuffer.length()
						|| inFileBuffer.charAt(filePointer) != '\n') {
					line++;
					column = 1;
					filePointer++;
				}
			} else if (inChar == '\n') {
				line++;
				column = 1;
				filePointer++;
			} else {
				if (inChar == '}') {
					commentFinished = true;
				}
				filePointer++;
			}

		}// end while

		if (!commentFinished) {
			TokenPair tp = new TokenPair("MP_RUN_COMMENT", "", line, column);
			addTokenToQueue(tp);
		}
	}

	private void moveBackChar() {
		filePointer--;
	}

	// This is why Character objects are so damn sexy!

	private boolean isLetter(char c) {
		return Character.isLetter(c);
	}

	private boolean isDigit(char c) {
		return Character.isDigit(c);
	}

	private boolean isWhiteSpace(char c) {
		return Character.isWhitespace(c);
	}

}
