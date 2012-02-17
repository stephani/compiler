package compiler;


public class Parser {

	private String lookAhead;
	
	protected void Program() { //rule 1
		if (lookAhead == "MP_SCOLON")  {
			match(";");
			ProgramHeading();
			Block();
			match(".");
		}
		else {
			System.out.println("Erro in Program() has not been implemented yet");
		}
	}
	protected void ProgramHeading() { //rule 2
		if (lookAhead == "program") {
			match("program");
			Identifier();
		}
		else {
			System.out.println("Error in ProgramHeading() has not been implemented yet");
		}
	}
	
	protected void Block() {  // rule 3. derek?
		System.out.println("Block() has not been implemented yet");
	}
	protected void StatementSequence() { //18 derek
		
	}
	protected void Statement() { //19  derek
		
	}

	//rule 25
	protected void ReadStatement() {
		if (lookAhead == "MP_READ") {
			match("read");
			ReadParameterList();
		}
		else {
			System.out.println("Error in ReadStatement() has not been implemented yet");
		}
	}
	//rule 26
	protected void WriteStatement() {
		if (lookAhead == "MP_WRITE") {
			match("write");
			WriteParameterList();
		}

		else {
			System.out.println("Error in WriteStatement() has not been implemented yet");
		}
	}
	//rule 27--not sure if this one is correct, I feel it's really wrong
	protected void AssignmentStatement() {
		if (lookAhead == "MP_ASSIGN") {
			Variable();
			FunctionIdentifier();
			match(":=");
			Expression();			
		}
		else {
			System.out.println("Error in AssignmentStatement() has not been implemented yet");
		}
		
	}
	//rule 28
	protected void ProcedureStatement() {
		ProcedureIdentifier();
		ActualParameterList();
		
	}
	//rule 29
	protected void IfStatement(){
		if (lookAhead == "MP_IF") {
			match("if");
			BooleanExpression();
		}
		else if (lookAhead == "MP_THEN"){
			match("then");
			Statement();
		}
		else if (lookAhead == "MP_ELSE"){
			match("else");
			Statement();
		}
		else {
			System.out.println("Error in IfStatement() has not been implemented yet");
		}
	}
	//rule 30
	protected void RepeatStatement() {
		if(lookAhead == "MP_REPEAT"){
			match("repeat");
			StatementSequence();
		}
		else if (lookAhead == "MP_UNTIL") {
			match("until");
			BooleanExpression();
		}
		else {
			System.out.println("Error in RepeatStatement() has not been implemented yet");
		}
	}
	//rule 31
	protected void WhileStatement() {
		if (lookAhead == "MP_WHILE"){
			match("while");
			BooleanExpression();
		}
		else if (lookAhead == "MP_DO"){
			match("do");
			Statement();
		}
		else {
			System.out.println("Error in WhileStatement() has not been implemented yet");
		}
	}
	//rule 32
	protected void ForStatement() {
		if (lookAhead == "MP_FOR") {
			match("for");
			ControlVariable();
		}
		else if (lookAhead == "MP_ASSIGN") {
			InitialValue();	
		}
		else if (lookAhead == "MP_TO" || lookAhead == "MP_DOWNTO") {
			match("to");
			match("downto");
			FinalValue();
		}
		else if (lookAhead =="MP_DO"){
			match("do");
			Statement();
		}
		else {
			System.out.println("Error in ForStatement() has not been implemented yet");
		}
	}
	//rule 33
	protected void ControlVariable() {
		if (lookAhead == "") {
			match("");
			VariableIdentifier();
		}
		else {
			System.out.println("Error in ControlVariable() has not been implemented yet");
		}
	}
	//rule 34
	protected void InitialValue() {
		if (lookAhead == ""){
			match("");
			OrdinalExpression();
		}
		else {
			System.out.println("Error in InitialValue() has not been implemented yet");
		}
	}
	//rule 35
	protected void FinalValue() {
		if (lookAhead == "") {
			match("");
			OrdinalExpression();
		}
		else {
			System.out.println("Error in FinalValue() has not been implemented yet");
		}
	}	
	//rule 36
	protected void Expression() {
		if (lookAhead == ""){
			match("");
			SimpleExpression();
			RelationalOperator();
			SimpleExpression();
		}
		else {
			System.out.println("Error in Expression() has not been implemented yet");
		}
	}
	//rule 37
	protected void SimpleExpression() {
		if (lookAhead == "") {
			match("");
			Sign();
			Term();
			AddingOperator();
			Term();
		}
		else {
			System.out.println("Error in SimpleExpression() has not been implemented yet");
		}
	}
	//rule 38
	protected void Term() {
		if (lookAhead == "") {
			match("");
			Factor();
			MultiplyingOperator();
			Factor();
		}
		else {
			System.out.println("Error in Term() has not been implemented yet");
		}
	}

	protected void Factor() { //39 rob
		
	}
	protected void RelationalOperator() { //40 rob
		
	}
	protected void AddingOperator() {  //41 rob
		
	}
	protected void MultiplyingOperator() { //42 rob
		
	}
	protected void Variable() { //44. rob?
		
	}
	protected void ActualParameterList() { //45
		
	}
	protected void ReadParameterList() { //47  rob?
		System.out.println("ReadParameterList() has not been implemented yet");
	}
	
	protected void WriteParameterList() { //49  rob?
		System.out.println("WriteParameterList() has not been implemented yet");
	}
	protected void BooleanExpression() {  //51 rob
		
	}
	protected void OrdinalExpression() { //52 rob
		
	}
	protected void VariableIdentifier() { //53 rob
		
	}
	protected void ProcedureIdentifier() { //54. rob
		
	}
	protected void FunctionIdentifier() {  //55. rob?
		
	}
	protected void Identifier() { //57 rob
		
		System.out.println("Identifier() has not been implemented yet");
	}
	protected void Sign() { //59
		
	}
	
	//need pre and post conditions
	private boolean match(String s){
	
		return false;
	}
	
}
