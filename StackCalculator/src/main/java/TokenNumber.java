
public class TokenNumber extends Token {
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
    public Tokens.TokenType getType() {
        return Tokens.TokenType.Number;
    }

    @Override
    public String toString() {
        return "" + number;
    }
}
