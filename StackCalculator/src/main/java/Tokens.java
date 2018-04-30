public class Tokens {
    public enum OpType {
        Plus, Minus, Multiply, Divide, Modular, Exponent, UnaryMinus, None;
    };

    public enum TokenType {
        Number, Operator, OpeningBracket, ClosingBracket, None,
    }

    public static int compareToOp(Token a, Token b) {
        OpType a_op = a.getType() == TokenType.Operator ? a.asOperator().getOperator() : OpType.None;
        OpType b_op = b.getType() == TokenType.Operator ? b.asOperator().getOperator() : OpType.None;
        int a_n = getPrecendenceOfOperator(a_op), b_n = getPrecendenceOfOperator(b_op);
        int diff = a_n - b_n;
        if (diff != 0)
            return diff;
        else if (a_op == OpType.Exponent || a_op == OpType.UnaryMinus)
            return 1;
        else
            return -1;
    }

    private static int getPrecendenceOfOperator(OpType c) {
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
}
