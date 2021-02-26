Package Project1;

Helpers
    letter = ['A'..'Z'] | ['a'..'z'];
    digit = ['0'..'9'];
    lf = 10 | 13;
    sp = ' ';
Tokens
    if = 'if';
    whitespace = lf | sp;
    number = digit+;
    real_number = number '.' digit*;
    comment = '//' .* lf | '/*' .* '*/';
    identifier = letter (digit | letter | '_')*;
