import java.util.List;		// used by expression evaluator
import java.util.ArrayList;

/**
 *	<Description goes here>
 *
 *	@author Anirudh Cherukuri
 *	@since	February 28, 2023
 */
public class SimpleCalc {

	private ExprUtils utils;	// expression utilities

	private ArrayStack<Double> valueStack;		// value stack
	private ArrayStack<String> operatorStack;	// operator stack
	private List<Double> vals;
	private List<String> vars;

	// constructor	
	public SimpleCalc() {
		utils = new ExprUtils();
		valueStack = new ArrayStack<Double>();
		operatorStack= new ArrayStack<String>();
		vals = new ArrayList<Double>();
		vars = new ArrayList<String>();

		vars.add("e"); vals.add(Math.E);
		vars.add("pi"); vals.add(Math.PI);
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
			else if(input.equals("l")) {
				listVars();
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
		System.out.println("  h - this message\n  q - quit\n  l - list variables\n");
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
		boolean equation = false;
		for(int i = 0; i < tokens.size(); i++) {
			if(tokens.get(i).equals("=")) equation = true;
		}

		if(equation) {
			boolean var = false;
			for(int i = 0; i < vars.size(); i++) {
				if(vars.get(i).equals(tokens.get(0))) var = true;
			}

			if(var) {
				int index = getIndex(tokens);
				double ans = evaluateExpression(tokens.subList(2, tokens.size()));
				vals.set(index, ans);
				System.out.printf("  %-7s= ", tokens.get(0));
				return vals.get(index);
			}
			else {
				vars.add(tokens.get(0));
				vals.add(evaluateExpression(tokens.subList(2, tokens.size())));
				System.out.printf("  %-7s= ", tokens.get(0));
				return vals.get(vals.size() - 1);
			}
		}

		for(int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			String operator = "";
			if(!operatorStack.isEmpty()) operator = operatorStack.peek();

			switch(token) {
				case "(":
					operatorStack.push(token);
					break;
				case ")":
					while(!operatorStack.peek().equals("(")) {
						valueStack.push(doMathInParenthesis(valueStack.pop(), valueStack.pop(), operatorStack.pop()));
					}
					operatorStack.pop();
					break;
				case "+":
				case "-":
				case "*":
				case "/":
				case "%":
				case "^":
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
		double value = 0;
		while(!valueStack.isEmpty()) {
			double num1 = valueStack.pop();
			//~ System.out.println("num1 = " + num1);
			if(!valueStack.isEmpty()) {
				double num2 = valueStack.pop();
				//~ System.out.println("num2 = " + num2);
				String operator = operatorStack.pop();
				//~ System.out.println("operator = " + operator);
				switch (operator) {
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
					case "^":
						value += Math.pow(num2, num1);
				}
			}
			else return num1;
		}

		return value;
	}

	/**
	 * 	Perform one operation
	 * 	@param	num1	Left number
	 *  @param 	num2	Right number
	 *  @param	op		Operator
	 *  @return			Solution to operation
	 */
	public double doMathInParenthesis(double num2, double num1, String op) {
		if (op.equals("+"))
			return num1 + num2;
		else if (op.equals("-"))
			return num1 - num2;
		else if (op.equals("*"))
			return num1 * num2;
		else if (op.equals("/"))
			return num1 / num2;
		else if (op.equals("%"))
			return num1 % num2;
		else
			return Math.pow(num1, num2);
	}

	public void listVars() {
		System.out.println("Variables");
		for(int i = 0; i < vars.size(); i++) {
			System.out.printf("%-8s%s %f\n", vars.get(i), "=", vals.get(i));
		}
	}

	public int getIndex(List<String> tokens) {
		int index;
		for(index = 0; index < vars.size(); index++) {
			if(vars.get(index).equals(tokens.get(0)))
				return index - 1;
		}
		return 0;
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