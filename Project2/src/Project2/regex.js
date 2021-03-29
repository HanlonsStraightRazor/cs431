Package Project2;

Helpers
    letter = ['a'..'z'] | ['A'..'Z'];
    digit = ['0'..'9'];
    linefeed = 10 | 13;
    space = 32;
    tab = 9 | 11;

Tokens
    semi = ';';
    equals = '<--';
    echo = 'echo' (linefeed | space | tab)*;
    lparen = '(';
    rparen = ')';
    num = digit+ ('.' digit+)?;
    comma = ',';
    add = '+';
    sub = '-';
    mul = '*';
    div = '/';
    mod = '%';
    lshift = '<<';
    rshift = '>>';
    id = letter+;
    whitespace = (linefeed | space | tab);
