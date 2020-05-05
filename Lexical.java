import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class Lexical {
	//variables
	static int charClass;
	static char[] lexeme = new char[100];
	static char nextChar;
	static int lexLen;
	static int tok;
	static int nextTok;
	static File in_fp;
	static int states = 0;

	//States
	static final int START = 0;
	static final int NUM = 1;
	static final int FL_NUM = 2;
	static final int STR = 3;
	
	//End states
	static final int END_N = 4;
	static final int END_F = 5;
	static final int END_S = 6;
	static final int END_I = 7;
	static final int END_E = 8;
	static final int END_EQ = 30;

	//Char Types
	static final int LETTER = 9;
	static final int DIGIT = 10;
	static final int EQUALITY = 11;
	static final int ADD_OP = 12;
	static final int MULT_OP = 13;
	static final int UNKNOWN = 99;
	static final int EOF = -1;

	//char values
	static final int STR_LIT = 12;
	static final int IDENT = 13;
	static final int INT_LIT = 14;
	static final int dot = 15;
	static final int FLO_LIT = 16;
  	static final int QUOTES = 17;
   	static final int ADD = 18;
	static final int SUB = 19;
	static final int MULT = 20;
	static final int DIV = 21;
	static final int LEFT_PAREN = 22;
	static final int RIGHT_PAREN = 23;
	static final int EQU = 24;
	
	
    //special characters
    static int Flag = 0;
    
	static public void state(int states) {
		switch(states) {
		case END_S:
			System.out.println("End state is: " + states + ", a String Literal.");
			break;
		case END_I:
			System.out.println("End state is: " + states + ", a Variable Name");
			break;
		case END_N:
			System.out.println("End state is: " + states + ", an Integer Literal.");
			break;
		case END_F:
			System.out.println("End state is: " + states + ", a Floating Point Literal.");
			break;
		}
	}
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
			nextTok = LEFT_PAREN;
			break;
		case ')':
			addChar();
			nextTok = RIGHT_PAREN;
			break;
		case '+':
			addChar();
			nextTok = ADD;
			break;
		case '-':
			addChar();
			nextTok = SUB;
			break;
		case '*':
			addChar();
			nextTok = MULT;
			break;
		case '/':
			addChar();
			nextTok = DIV;
			break;
		case '=':
			addChar();
			nextTok = EQU;
			break;
		case '>':
			addChar();
			nextTok = EQU;
			break;
		case '<':
			addChar();
			nextTok = EQU;
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
				states = STR;
			}
			else if (Character.isDigit(nextChar)) {
				charClass = DIGIT;
				states = NUM;
			}
			else {
				charClass = UNKNOWN;
			}
		} else {
			charClass = EOF;
			states = END_E;
		}
	}
	static void getNonBlank(BufferedReader br) throws IOException {
		while (Character.isWhitespace(nextChar)) {
			getChar(br);
			}
		states = START;
		
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
			states = END_I;
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
					states = END_F;
					break;
				}		
			}
			nextTok = INT_LIT;
			states = END_N;
			break;
		/* equality operator */
		case EQUALITY:
			addChar();
			getChar(br);
			while(charClass == EQUALITY) {
				addChar();
				getChar(br);
			}
			nextTok = EQU;
			states = END_EQ;
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
				states = END_S;
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
				states = END_F;
			}
			break;
			/* EOF */
		case EOF:
			nextTok = 0;
			states = END_E;
			lexeme[0] = 0;
			break;
		} /* End of switch */
		System.out.print("Lexeme is: ");
		for(int i=0; i<lexeme.length;i++) {
			System.out.print(lexeme[i]);
		}
		System.out.print("\n");
		state(states);
		System.out.print("\n");
		return nextTok;
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
		 } while (nextTok != 0);
		 br.close();
		 }
	}	
}
