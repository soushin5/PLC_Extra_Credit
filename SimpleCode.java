/* BNF: SimpleCode: Boolean, Assignment, Math
 * <assign> -> <assign> = <logic>| <logic>
 * <logic>  -> <logic> && <equal> | <logic> || <equal> | <equal>          
 * <equal>  -> <equal> == <rel> | <equal> != <rel> | <rel>
 * <rel>    -> <rel> < <add> | <rel> <= <add> | <rel> > <add> | <rel> >= <add> | <add>
 * <add>    -> <add> + <mult> | <add> - <mult> | <mult>
 * <mult>   -> <mult> * <term> | <mult> / <term> | <mult> % <term> | <term>
 * <term>   -> (<add>) | int_lit | id
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class SimpleCode {
	//variables
	static int charClass;
	static char[] lexeme = new char[100];
	static char nextChar;
	static int lexLen;
	static int tok;
	static int nextTok;
	static File in_fp;
	static int states = 0;

	//Char Types
	static final int LETTER = 0;
	static final int DIGIT = 1;
	static final int UNKNOWN = 99;
	static final int EOF = -1;

	//char values
	static final int STR_LIT = 1;
	static final int IDENT = 2;
	static final int INT_LIT = 3;
	static final int FLO_LIT = 4;
	static final int dot = 5;

	static final int QUOTES = 6;
	static final int DUB_EQ = 10;
	static final int NOT_EQ = 11;
	static final int EQ = 12;
	static final int LE = 13;
	static final int GR = 15;
	static final int LE_EQ = 16;
	static final int GR_EQ = 17;
	static final int ADD_OP = 18;
	static final int SUB_OP = 19;
	static final int MULT_OP = 20;
	static final int DIV_OP = 21;
	static final int MOD_OP = 22;
	static final int L_PARE = 25;
	static final int R_PARE = 26;
	static final int AND_OP =27;
	static final int OR_OP = 28;

	//special characters
	static int Flag = 0;


	static int lookup(char ch){
		switch (ch) {
		case '.':
			addChar();
			nextTok = dot;
			break;
		case '"':
			addChar();
			nextTok = QUOTES;
			break;
		case '(':
			addChar();
			nextTok = L_PARE;
			break;
		case ')':
			addChar();
			nextTok = R_PARE;
			break;
		case '+':
			addChar();
			nextTok = ADD_OP;
			break;
		case '-':
			addChar();
			nextTok = SUB_OP;
			break;
		case '*':
			addChar();
			nextTok = MULT_OP;
			break;
		case '/':
			addChar();
			nextTok = DIV_OP;
			break;
		case '%':
			addChar();
			nextTok = MOD_OP;
			break;
		default:
			addChar();
			nextTok = 0;
			break;
		}
		return nextTok;
	}

	static void addChar(){
		if (lexLen <= 98) {
			lexeme[lexLen++] = nextChar;
			lexeme[lexLen] = 0;
		} else {
			System.out.println("Error - lexeme is too long \n");
		}
	}
	static void getChar(BufferedReader br) throws IOException{
		int nc;
		char f = 'f';
		char e = 'e';
		if ((nc = br.read()) != -1) { 
			nextChar = (char) nc;
			if (Character.isLetter(nextChar)) {
				if(nextChar == e) {
					Flag = 1;
				}
				if(nextChar == f) {
					Flag = 2;
				}
				charClass = LETTER;
			}
			else if (Character.isDigit(nextChar)) {
				charClass = DIGIT;
			}
			else {
				charClass = UNKNOWN;
			}
		} else {
			charClass = EOF;
		}
	}

	static void getNonBlank(BufferedReader br) throws IOException {
		while (Character.isWhitespace(nextChar)) {
			getChar(br);
		}
	}

	static int lex(BufferedReader br) throws IOException {
		lexeme = new char[lexeme.length];
		lexLen = 0;
		getNonBlank(br);
		switch (charClass) {
		/* Identifiers */
		case LETTER:
			addChar();
			getChar(br);
			while (charClass == LETTER || charClass == DIGIT) {
				addChar();
				getChar(br);
			}
			nextTok = IDENT;
			break;
			/* Integer literals and Float Literals*/
		case DIGIT:
			addChar();
			getChar(br);
			while (charClass == DIGIT) {
				addChar();
				getChar(br);
			}
			if(charClass == UNKNOWN) {
				lookup(nextChar);
				if(nextTok == dot) {
					getChar(br);
					while(charClass == DIGIT || (charClass == LETTER && Flag == 1)) {
						addChar();
						getChar(br);
					}
					if(charClass == LETTER && Flag == 2) {
						addChar();
						getChar(br);
					}
					nextTok = FLO_LIT;
					break;
				}		
			}
			nextTok = INT_LIT;
			break;
			/*quotes for Strings*/
		case UNKNOWN:
			lookup(nextChar);
			if(nextTok == QUOTES) {
				getChar(br);
				while (charClass == LETTER || charClass == DIGIT) {
					addChar();
					getChar(br);
				}
				addChar();
				getChar(br);
				nextTok = STR_LIT;	
			}
			if(nextTok == dot) {
				getChar(br);
				while(charClass == DIGIT || (charClass == LETTER && Flag == 1)) {
					addChar();
					getChar(br);
				}
				if(charClass == LETTER && Flag == 2) {
					addChar();
					getChar(br);
				}
				nextTok = FLO_LIT;
			}
			break;
			/* EOF */
		case EOF:
			nextTok = 0;
			lexeme[0] = 0;
			break;
		} /* End of switch */
		System.out.print("Lexeme is: ");
		for(int i=0; i<lexeme.length;i++) {
			System.out.print(lexeme[i]);
		}
		System.out.print("\n");
		System.out.print("\n");
		return nextTok;
	}

	public static void assign(BufferedReader br) throws IOException {
		logic(br);
		while (nextTok == EQ) {
			lex(br);
			logic(br);
		}
	}

	public static void logic(BufferedReader br) throws IOException {
		equal(br);
		while (nextTok == AND_OP || nextTok == OR_OP) {
			lex(br);
			equal(br);
		}
	}
	public static void equal(BufferedReader br) throws IOException {
		rel(br);
		while (nextTok == DUB_EQ || nextTok == NOT_EQ) {
			lex(br);
			rel(br);
		}
	}

	public static void rel(BufferedReader br) throws IOException {
		add(br);
		while (nextTok == LE || nextTok == LE_EQ || nextTok == GR || nextTok == GR_EQ) {
			lex(br);
			add(br);
		}
	}

	public static void add(BufferedReader br) throws IOException {
		mult(br);
		while (nextTok == ADD_OP || nextTok == SUB_OP) {
			lex(br);
			mult(br);
		}
	}
	public static void mult(BufferedReader br) throws IOException {
		term(br);
		while (nextTok == MULT_OP || nextTok == DIV_OP || nextTok == MOD_OP) {
			lex(br);
			term(br);
		}
	}

	public static void term(BufferedReader br) throws IOException {
		if (nextTok == IDENT || nextTok == INT_LIT) {
			lex(br);
		} else {
			if (nextTok == L_PARE) {
				lex(br);
				add(br);
				if (nextTok == R_PARE) {
					lex(br);
				}
				else {
					error();
				}
			} else
				error();
		}
	}
	
	static void error() {
		System.out.println("Error = Symbol not found");
	}

	public static void main(String[]args) throws IOException{
		System.out.println("State Diagram Recognition");
		if ((in_fp = new File("C:\\Users\\kuwan_000\\Documents"
				+ "\\Georgia State\\GA State Spring 2020\\Programming Language Concepts\\final_1test.txt")) == null) {
			System.out.println("ERROR - cannot open file \n");
		}
		else {
			BufferedReader br = new BufferedReader(new FileReader(in_fp));
			getChar(br);
			do {
				lex(br);
				assign(br);
			} while (nextTok != 0);
			br.close();
		}
	}	
}
