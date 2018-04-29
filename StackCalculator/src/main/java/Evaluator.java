import java.util.Stack;

public class Evaluator {
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
        if (t.getType() != Tokens.TokenType.Number)
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
        if (t.getType() == Tokens.TokenType.Operator)
            applyOperatorAndPushResultToStack(t.asOperator());
        else if (t.getType() == Tokens.TokenType.Number)
            pushNumberToStack(t.asNumber());
        else
            throw new IllegalArgumentException("Illegal token on evaluation");
    }

    public long evaluate() {
        for (Token t : tokens)
            processToken(t);

        if (stack.isEmpty() || stack.peek().getType() != Tokens.TokenType.Number)
            throw new IllegalArgumentException("Mismatched operator and operand");

        long result = stack.pop().asNumber().getNumber();

        if (!stack.isEmpty())
            throw new IllegalArgumentException("Mismatched operator and operand");

        return result;
    }

}
