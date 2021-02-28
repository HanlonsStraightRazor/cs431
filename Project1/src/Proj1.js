Package Project1;

Helpers
    letter = ['A'..'Z'] | ['a'..'z'];
    digit = ['0'..'9'];
    lf = 10 | 13;
    sp = ' ';
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
    whitespace = (lf | sp);
    number = digit+ '.' digit*;
    comments = ('//' any* lf) | ('/*' any* '*/');
    id = letter (digit | letter | '_')*;

Ignored Tokens
    whitespace;