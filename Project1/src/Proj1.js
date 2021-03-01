Package Project1;

Helpers
    letter = ['A'..'Z'] | ['a'..'z'];
    digit = ['0'..'9'];
    lf = 10 | 12 | 13;
    sp = ' ';
    tabs = 9 | 11;
    any = [0..255];

Tokens
    class = 'class';
    public = 'public';
    static = 'static';
    void = 'void';
    main = 'main';
    this = 'this';
    return = 'return';
    if = 'if';
    else = 'else';
    add = '+';
    minus = '-';
    times = '*';
    lparen = '(';
    rparen = ')';
    lbracket = '[';
    rbracket = ']';
    lcurly = '{';
    rcurly = '}';
    print = 'System.out.println';
    new = 'new';
    period = '.';
    semicolon = ';';
    equal = '=';
    whitespace = (lf | sp | tabs);
    number = digit+;
    real_numbers = digit+ '.' digit*;
    comments = ('//' any* lf) | ('/*' any* '*/');
    id = letter (digit | letter | '_')*;

Ignored Tokens
    whitespace;
