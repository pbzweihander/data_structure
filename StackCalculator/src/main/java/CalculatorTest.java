import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

public class CalculatorTest {
    public enum OpType {
        Plus, Minus, Multiply, Divide, Modular, Exponent, UnaryMinus, None;
    };

    private enum TokenType {
        Number, Operator, Bracket, None,
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
            number = Integer.parseInt(s);
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
                return a.number / b.number;
            case Modular:
                return a.number % b.number;
            case Exponent:
                return (long) Math.pow(a.number, b.number);
            default:
                throw new IllegalArgumentException();
            }
        }

        public long apply(TokenNumber a) {
            if (operator == OpType.UnaryMinus)
                return -a.getNumber();
            else
                throw new IllegalArgumentException();
        }

        public void applyWithStack(Stack<Token> stack) {
            if (stack.isEmpty())
                throw new IllegalArgumentException();
            Token a = stack.pop();
            if (a.getType() != TokenType.Number)
                throw new IllegalArgumentException();

            long result;
            if (operator == OpType.UnaryMinus)
                result = apply(a.asNumber());
            else {
                if (stack.isEmpty())
                    throw new IllegalArgumentException();
                Token b = stack.pop();
                if (b.getType() != TokenType.Number)
                    throw new IllegalArgumentException();
                result = apply(a.asNumber(), b.asNumber());
            }
            stack.push(new TokenNumber(result));
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
            return TokenType.Bracket;
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
            Token[] tokens = infixToPostfix(input);
            long result = evaluateInfix(tokens);
            System.out.println(result);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
    }

    private long evaluateInfix(Token[] tokens) {
        Stack<Token> stack = new Stack<>();

        for (Token t : tokens) {
            if (t.getType() == TokenType.Operator)
                t.asOperator().applyWithStack(stack);
            else if (t.getType() == TokenType.Number)
                stack.push(t);
            else
                throw new IllegalArgumentException();
        }

        if (stack.isEmpty() || stack.peek().getType() != TokenType.Number)
            throw new IllegalArgumentException();
        long result = stack.pop().asNumber().getNumber();
        if (!stack.isEmpty())
            throw new IllegalArgumentException();
        return result;
    }

    private Token[] infixToPostfix(String expr) {
        Stack<Token> stack = new Stack<>();
        ArrayList<Token> result = new ArrayList<>();

        StringBuilder acc = new StringBuilder();
        boolean acc_is_empty = true;
        boolean whitespace_after_acc = false;
        TokenType last = TokenType.None;

        for (char c : expr.toCharArray()) {
            if (Character.isDigit(c)) {
                if (whitespace_after_acc)
                    throw new IllegalArgumentException();

                acc.append(c);
                acc_is_empty = false;

                last = TokenType.Number;
            } else {
                if (!acc_is_empty) {
                    result.add(new TokenNumber(acc.toString()));
                    acc.setLength(0);
                    acc_is_empty = true;
                }

                if (!Character.isWhitespace(c)) {
                    if (c == '(') {
                        stack.push(new TokenBracket());

                        last = TokenType.Bracket;
                    } else if (c == ')') {
                        if (stack.peek().getType() == TokenType.Bracket)
                            throw new IllegalArgumentException();

                        while (!stack.isEmpty() && stack.peek().getType() != TokenType.Bracket)
                            result.add(stack.pop());

                        if (stack.isEmpty())
                            throw new IllegalArgumentException();
                        else
                            stack.pop();

                        last = TokenType.Bracket;
                    } else {
                        if ((last == TokenType.None || last == TokenType.Operator) && c == '-')
                            c = '~';
                        TokenOperator op = new TokenOperator(c);
                        if (op.getOperator() == OpType.None)
                            throw new IllegalArgumentException();
                        while (!stack.isEmpty() && compareToOp(op, stack.peek()) < 0)
                            result.add(stack.pop());
                        stack.push(op);

                        last = TokenType.Operator;
                    }

                    whitespace_after_acc = false;
                } else {
                    if (!acc_is_empty)
                        whitespace_after_acc = true;
                }
            }
        }

        if (!acc_is_empty)
            result.add(new TokenNumber(acc.toString()));
        while (!stack.isEmpty())
            result.add(stack.pop());

        return result.toArray(new Token[0]);
    }
}
