import java.util.List;		// used by expression evaluator

/**
 *	<Description goes here>
 *
 *	@author	
 *	@since	
 */
public class SimpleCalc {
	
	private ExprUtils utils;	// expression utilities
	
	private ArrayStack<Double> valueStack;		// value stack
	private ArrayStack<String> operatorStack;	// operator stack

	// constructor	
	public SimpleCalc() {
		utils = new ExprUtils();
		valueStack = new ArrayStack<Double>();
		operatorStack= new ArrayStack<String>();
	}
	
	public static void main(String[] args) {
		SimpleCalc sc = new SimpleCalc();
		sc.run();
	}
	
	public void run() {
		System.out.println("\nWelcome to SimpleCalc!!!\n");
		runCalc();
		System.out.println("\nThanks for using SimpleCalc! Goodbye.\n");
	}
	
	/**
	 *	Prompt the user for expressions, run the expression evaluator,
	 *	and display the answer.
	 */
	public void runCalc() {
		String input = "";
		while(!input.equals("q")) {
			double ans = 0.0;
			input = Prompt.getString("");
			if(input.equals("h")) {
				printHelp();
				System.out.println();
			}
			else if(!input.equals("q")) {
				ans = evaluateExpression(utils.tokenizeExpression(input));
				System.out.println(ans);
			}
		}
	}
	
	/**	Print help */
	public void printHelp() {
		System.out.println("Help:");
		System.out.println("  h - this message\n  q - quit\n");
		System.out.println("Expressions can contain:");
		System.out.println("  integers or decimal numbers");
		System.out.println("  arithmetic operators +, -, *, /, %, ^");
		System.out.println("  parentheses '(' and ')'");
	}
	
	/**
	 *	Evaluate expression and return the value
	 *	@param tokens	a List of String tokens making up an arithmetic expression
	 *	@return			a double value of the evaluated expression
	 */
	public double evaluateExpression(List<String> tokens) {
		double value = 0.0;
		
		for(int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			String operator = "";
			if(!operatorStack.isEmpty()) operator = operatorStack.peek();
			
			switch(token) {
				case "+":
				case "-":
				case "*":
				case "/":
				case "%":
				case "(":
				case ")":
					if(hasPrecedence(token, operator))
						valueStack.push(doMath());
					
					operatorStack.push(token);
					break;
				default:
					valueStack.push(Double.parseDouble(token));
					break;
			}
		}
		
		value += doMath();
		
		return value;
	}
	
	/**
	 * 	Computes answer using both stacks
	 * 	@return		answer to operation
	 */
	public double doMath() {
		int value = 0;
		while(!valueStack.isEmpty()) {
			double num1 = valueStack.pop();
			//~ System.out.println("num1 = " + num1);
			if(!valueStack.isEmpty()) {
				double num2 = valueStack.pop();
				//~ System.out.println("num2 = " + num2);
				String operator = operatorStack.pop();
				//~ System.out.println("operator = " + operator);
				
				switch(operator) {
					case "+":
						value += num2 + num1;
						valueStack.push(num2 + num1);
						break;
					case "-":
						value += num2 - num1;
						valueStack.push(num2 - num1);
						break;
					case "*":
						value += num2 * num1;
						valueStack.push(num2 * num1);
						break;
					case "/":
						value += num2 / num1;
						valueStack.push(num2 / num1);
						break;
					case "%":
						value += num2 % num1;
						valueStack.push(num2 % num1);
						break;
				}
			} else return num1;
		}
		
		return value;
	}
	
	/**
	 *	Precedence of operators
	 *	@param op1	operator 1
	 *	@param op2	operator 2
	 *	@return		true if op2 has higher or same precedence as op1; false otherwise
	 *	Algorithm:
	 *		if op1 is exponent, then false
	 *		if op2 is either left or right parenthesis, then false
	 *		if op1 is multiplication or division or modulus and 
	 *				op2 is addition or subtraction, then false
	 *		otherwise true
	 */
	private boolean hasPrecedence(String op1, String op2) {
		if (op1.equals("^")) return false;
		if (op2.equals("(") || op2.equals(")")) return false;
		if ((op1.equals("*") || op1.equals("/") || op1.equals("%")) 
				&& (op2.equals("+") || op2.equals("-")))
			return false;
		return true;
	}
	 
}
