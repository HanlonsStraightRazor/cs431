Package Project1;

Helpers
    letter = ['A'..'Z'] | ['a'..'z'];
    digit = ['0'..'9'];
    lf = 10 | 12 | 13;
    sp = ' ';
    tabs = 9 | 11;
    notlf = [0..9] | 11 | [14..255];
    notstar = [0..41] | [43..46] | [48..255];

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
    string = 'String';
    int = 'int';
    boolean = 'boolean';
    add = '+';
    minus = '-';
    times = '*';
    lparen = '(';
    rparen = ')';
    lbracket = '[';
    rbracket = ']';
    lcurly = '{';
    rcurly = '}';
    lt = '<';
    gt = '>';
    comma = ',';
    exc = '!';
    and = '&&';
    print = 'System.out.println';
    new = 'new';
    period = '.';
    semicolon = ';';
    equal = '=';
    whitespace = (lf | sp | tabs);
    number = digit+;
    real_numbers = digit+ '.' digit*;
    comments = ('//' notlf* lf) | ('/*' (notstar* '*'+)+ '/');
    id = letter (digit | letter | '_')*;
