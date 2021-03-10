Package Project2;

Helpers
    letter = ['a'..'z'] | ['A'..'Z'];
    digit = ['0'..'9'];
    linefeed = 10 | 13;
    space = 32;
    tab = 9 | 11;

Tokens
    semi = ';';
    id = letter+;
    equals = '<--';
    echo = 'echo';
    lparen = '(';
    rparen = ')';
    num = digit+ ('.' digit+)?;
    add = '+';
    sub = '-';
    mul = '*';
    div = '/';
    mod = '%';
    lshift = '<<';
    rshift = '>>';
    whitespace = (linefeed | space | tab);
