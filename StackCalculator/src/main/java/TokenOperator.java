
public class TokenOperator extends Token {
    private Tokens.OpType operator;

    public TokenOperator(char c) {
        switch (c) {
        case '+':
            operator = Tokens.OpType.Plus;
            break;
        case '-':
            operator = Tokens.OpType.Minus;
            break;
        case '*':
            operator = Tokens.OpType.Multiply;
            break;
        case '/':
            operator = Tokens.OpType.Divide;
            break;
        case '%':
            operator = Tokens.OpType.Modular;
            break;
        case '^':
            operator = Tokens.OpType.Exponent;
            break;
        case '~':
            operator = Tokens.OpType.UnaryMinus;
            break;
        default:
            operator = Tokens.OpType.None;
        }
        origin = c;
    }

    public Tokens.OpType getOperator() {
        return operator;
    }

    public long apply(TokenNumber a, TokenNumber b) {
        switch (operator) {
        case Plus:
            return a.getNumber() + b.getNumber();
        case Minus:
            return a.getNumber() - b.getNumber();
        case Multiply:
            return a.getNumber() * b.getNumber();
        case Divide:
            if (b.getNumber() == 0)
                throw new IllegalArgumentException("Arithmetic error: cannot divide with 0");
            return a.getNumber() / b.getNumber();
        case Modular:
            if (b.getNumber() == 0)
                throw new IllegalArgumentException("Arithmetic error: cannot divide with 0");
            return a.getNumber() % b.getNumber();
        case Exponent:
            if (a.getNumber() == 0 && b.getNumber() < 0)
                throw new IllegalArgumentException("Arithmetic error: cannot power 0 with negative number");
            return (long) Math.pow(a.getNumber(), b.getNumber());
        default:
            throw new IllegalArgumentException("Illegal operator");
        }
    }

    public long apply(TokenNumber a) {
        if (operator == Tokens.OpType.UnaryMinus)
            return -a.getNumber();
        else
            throw new IllegalArgumentException();
    }

    public boolean isUnaryOperator() {
        return operator == Tokens.OpType.UnaryMinus;
    }

    @Override
    public Tokens.TokenType getType() {
        return Tokens.TokenType.Operator;
    }
}
