import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;

public class CalculatorTest {
    private enum OpType {
        Plus, Minus, Multiply, Divide, Modular, Exponent, UnaryMinus, None;
    };

    private enum TokenType {
        Number, Operator, OpeningBracket, ClosingBracket, None,
    }

    private abstract class Token {
        public char origin;

        public abstract TokenType getType();

        public TokenNumber asNumber() {
            return (TokenNumber) this;
        }

        public TokenOperator asOperator() {
            return (TokenOperator) this;
        }

        @Override
        public String toString() {
            return "" + origin;
        }
    }

    private class TokenNumber extends Token {
        private long number;

        public TokenNumber(String s) {
            number = Long.parseLong(s);
            origin = s.charAt(s.length() - 1);
        }

        public TokenNumber(long l) {
            number = l;
            origin = '\0';
        }

        public long getNumber() {
            return number;
        }

        @Override
        public TokenType getType() {
            return TokenType.Number;
        }

        @Override
        public String toString() {
            return "" + number;
        }
    }

    private class TokenOperator extends Token {
        private OpType operator;

        public TokenOperator(char c) {
            switch (c) {
            case '+':
                operator = OpType.Plus;
                break;
            case '-':
                operator = OpType.Minus;
                break;
            case '*':
                operator = OpType.Multiply;
                break;
            case '/':
                operator = OpType.Divide;
                break;
            case '%':
                operator = OpType.Modular;
                break;
            case '^':
                operator = OpType.Exponent;
                break;
            case '~':
                operator = OpType.UnaryMinus;
                break;
            default:
                operator = OpType.None;
            }
            origin = c;
        }

        public OpType getOperator() {
            return operator;
        }

        public long apply(TokenNumber a, TokenNumber b) {
            switch (operator) {
            case Plus:
                return a.number + b.number;
            case Minus:
                return a.number - b.number;
            case Multiply:
                return a.number * b.number;
            case Divide:
                if (b.number == 0)
                    throw new IllegalArgumentException("Arithmetic error: cannot divide with 0");
                return a.number / b.number;
            case Modular:
                if (b.number == 0)
                    throw new IllegalArgumentException("Arithmetic error: cannot divide with 0");
                return a.number % b.number;
            case Exponent:
                return (long) Math.pow(a.number, b.number);
            default:
                throw new IllegalArgumentException("Illegal operator");
            }
        }

        public long apply(TokenNumber a) {
            if (operator == OpType.UnaryMinus)
                return -a.getNumber();
            else
                throw new IllegalArgumentException();
        }

        public boolean isUnaryOperator() {
            return operator == OpType.UnaryMinus;
        }

        @Override
        public TokenType getType() {
            return TokenType.Operator;
        }
    }

    private class TokenBracket extends Token {
        public TokenBracket() {
            origin = '(';
        }

        @Override
        public TokenType getType() {
            return TokenType.OpeningBracket;
        }
    }

    private static int compareToOp(Token a, Token b) {
        OpType a_op = a.getType() == TokenType.Operator ? a.asOperator().getOperator() : OpType.None;
        OpType b_op = b.getType() == TokenType.Operator ? b.asOperator().getOperator() : OpType.None;
        int a_n = prec(a_op), b_n = prec(b_op);
        int diff = a_n - b_n;
        if (diff != 0)
            return diff;
        else if (a_op == OpType.Exponent || a_op == OpType.UnaryMinus)
            return 1;
        else
            return -1;
    }

    private static int prec(OpType c) {
        switch (c) {
        case Plus:
        case Minus:
            return 1;
        case Multiply:
        case Divide:
        case Modular:
            return 2;
        case UnaryMinus:
            return 3;
        case Exponent:
            return 4;
        default:
            return -1;
        }
    }

    private class Parser {
        private String expr;
        private Stack<Token> stack;
        private ArrayList<Token> result;
        private StringBuilder number_acc;
        private boolean is_acc_empty;
        private boolean is_whitespace_after_acc;
        private TokenType last_token;

        public Parser(String expr) {
            this.expr = expr;
            stack = new Stack<>();
            result = new ArrayList<>();
            number_acc = new StringBuilder();
            is_acc_empty = true;
            is_whitespace_after_acc = false;
            last_token = TokenType.None;
        }

        private void accumulateNumber(char c) {
            if (is_whitespace_after_acc)
                throw new IllegalArgumentException("Whitespace between number");

            number_acc.append(c);
            is_acc_empty = false;

            last_token = TokenType.Number;
        }

        private void pushAccToStackAsNumber() {
            result.add(new TokenNumber(number_acc.toString()));
            number_acc.setLength(0);
            is_acc_empty = true;
        }

        private void pushOpeningBracketToStack() {
            stack.push(new TokenBracket());

            last_token = TokenType.OpeningBracket;
        }

        private void popUntilOpeningBracket() {
            if (last_token == TokenType.OpeningBracket)
                throw new IllegalArgumentException("Empty parenthesis");

            while (!stack.isEmpty() && stack.peek().getType() != TokenType.OpeningBracket)
                result.add(stack.pop());

            if (stack.isEmpty())
                throw new IllegalArgumentException("Mismatched parenthesis");
            else
                stack.pop();

            last_token = TokenType.ClosingBracket;
        }

        private void evaluateOperator(char c) {
            TokenOperator op;
            if (last_token != TokenType.Number && last_token != TokenType.ClosingBracket && c == '-')
                op = new TokenOperator('~');
            else {
                op = new TokenOperator(c);
            }

            if (op.getOperator() == OpType.None)
                throw new IllegalArgumentException("Illegal operator");

            while (!stack.isEmpty() && compareToOp(op, stack.peek()) < 0)
                result.add(stack.pop());
            stack.push(op);

            last_token = TokenType.Operator;
        }

        private void processChar(char c) {
            if (Character.isDigit(c))
                accumulateNumber(c);
            else {
                if (!Character.isWhitespace(c)) {
                    if (!is_acc_empty)
                        pushAccToStackAsNumber();

                    if (c == '(')
                        pushOpeningBracketToStack();
                    else if (c == ')')
                        popUntilOpeningBracket();
                    else
                        evaluateOperator(c);

                    is_whitespace_after_acc = false;
                } else if (!is_acc_empty)
                    is_whitespace_after_acc = true;
            }
        }

        public Token[] parse() {
            for (char c : expr.toCharArray())
                processChar(c);

            if (!is_acc_empty)
                result.add(new TokenNumber(number_acc.toString()));
            while (!stack.isEmpty())
                result.add(stack.pop());

            return result.toArray(new Token[0]);
        }
    }

    private class Evaluator {
        private Token[] tokens;
        private Stack<Token> stack;

        public Evaluator(Token[] tokens) {
            this.tokens = tokens;
            stack = new Stack<>();
        }

        private TokenNumber popNumberFromStack() {
            if (stack.isEmpty())
                throw new IllegalArgumentException("Insufficient operand");
            Token t = stack.pop();
            if (t.getType() != TokenType.Number)
                throw new IllegalArgumentException("Illegal operand token type");

            return t.asNumber();
        }

        private long applyBinaryOperator(TokenOperator t) {
            TokenNumber a, b;
            b = popNumberFromStack();
            a = popNumberFromStack();

            return t.apply(a, b);
        }

        private long applyUnaryOperator(TokenOperator t) {
            return t.apply(popNumberFromStack());
        }

        private void applyOperatorAndPushResultToStack(TokenOperator t) {
            long result;
            if (t.isUnaryOperator())
                result = applyUnaryOperator(t);
            else
                result = applyBinaryOperator(t);

            stack.push(new TokenNumber(result));
        }

        private void pushNumberToStack(TokenNumber t) {
            stack.push(t);
        }

        private void processToken(Token t) {
            if (t.getType() == TokenType.Operator)
                applyOperatorAndPushResultToStack(t.asOperator());
            else if (t.getType() == TokenType.Number)
                pushNumberToStack(t.asNumber());
            else
                throw new IllegalArgumentException("Illegal token on evaluation");
        }

        public long evaluate() {
            for (Token t : tokens)
                processToken(t);

            if (stack.isEmpty() || stack.peek().getType() != TokenType.Number)
                throw new IllegalArgumentException("Mismatched operator and operand");

            long result = stack.pop().asNumber().getNumber();

            if (!stack.isEmpty())
                throw new IllegalArgumentException("Mismatched operator and operand");

            return result;
        }

    }

    public static void main(String args[]) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                String input = br.readLine();
                if (input.compareTo("q") == 0)
                    break;

                CalculatorTest instance = new CalculatorTest();
                instance.processInput(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processInput(String input) {
        try {
            Parser parser = new Parser(input);
            Token[] tokens = parser.parse();

            Evaluator evaluator = new Evaluator(tokens);
            long result = evaluator.evaluate();

            System.out.print(Arrays.stream(tokens).map(t -> t.toString()).collect(Collectors.joining(" ")));
            System.out.println();
            System.out.println(result);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR");
        }
    }
}
