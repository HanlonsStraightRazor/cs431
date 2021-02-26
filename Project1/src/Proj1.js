Package Project1;

Helpers
    letter = ['A'..'Z'] | ['a'..'z'];
    digit = ['0'..'9'];
    lf = 10 | 13;
    sp = ' ';
    any = [0..25];
Tokens
    if = 'if';
    whitespace = lf | sp;
    number = digit+;
    real_number = digit* '.' digit*;
    comment = ('//' any* lf) | ('/*' any* '*/');
    identifier = letter (digit | letter | '_')*;
