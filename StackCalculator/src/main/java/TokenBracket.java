
public class TokenBracket extends Token {
    public TokenBracket() {
        origin = '(';
    }

    @Override
    public Tokens.TokenType getType() {
        return Tokens.TokenType.OpeningBracket;
    }
}
