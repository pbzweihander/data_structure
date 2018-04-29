
public abstract class Token {
    public char origin;

    public abstract Tokens.TokenType getType();

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
