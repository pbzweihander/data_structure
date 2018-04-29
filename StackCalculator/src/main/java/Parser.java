import java.util.ArrayList;
import java.util.Stack;

public class Parser {
    private String expr;
    private Stack<Token> stack;
    private ArrayList<Token> result;
    private StringBuilder number_acc;
    private boolean is_acc_empty;
    private boolean is_whitespace_after_acc;
    private Tokens.TokenType last_token;

    public Parser(String expr) {
        this.expr = expr;
        stack = new Stack<>();
        result = new ArrayList<>();
        number_acc = new StringBuilder();
        is_acc_empty = true;
        is_whitespace_after_acc = false;
        last_token = Tokens.TokenType.None;
    }

    private void accumulateNumber(char c) {
        if (is_whitespace_after_acc)
            throw new IllegalArgumentException("Whitespace between number");

        number_acc.append(c);
        is_acc_empty = false;

        last_token = Tokens.TokenType.Number;
    }

    private void pushAccToStackAsNumber() {
        result.add(new TokenNumber(number_acc.toString()));
        number_acc.setLength(0);
        is_acc_empty = true;
    }

    private void pushOpeningBracketToStack() {
        stack.push(new TokenBracket());

        last_token = Tokens.TokenType.OpeningBracket;
    }

    private void popUntilOpeningBracket() {
        if (last_token == Tokens.TokenType.OpeningBracket)
            throw new IllegalArgumentException("Empty parenthesis");

        while (!stack.isEmpty() && stack.peek().getType() != Tokens.TokenType.OpeningBracket)
            result.add(stack.pop());

        if (stack.isEmpty())
            throw new IllegalArgumentException("Mismatched parenthesis");
        else
            stack.pop();

        last_token = Tokens.TokenType.ClosingBracket;
    }

    private void evaluateOperator(char c) {
        TokenOperator op;
        if (last_token != Tokens.TokenType.Number && last_token != Tokens.TokenType.ClosingBracket && c == '-')
            op = new TokenOperator('~');
        else {
            op = new TokenOperator(c);
        }

        if (op.getOperator() == Tokens.OpType.None)
            throw new IllegalArgumentException("Illegal operator");

        while (!stack.isEmpty() && Tokens.compareToOp(op, stack.peek()) < 0)
            result.add(stack.pop());
        stack.push(op);

        last_token = Tokens.TokenType.Operator;
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
