Package ParserProject;

Helpers
    lf = 10 | 12 | 13;
    sp = ' ';
    tabs = 9 | 11;
    digit = ['0'..'9'];
    letter = ['a'..'z'] | ['A'..'Z'];
    alphanumeric = letter | digit;

Tokens
    classs = 'CLASS';
    begin = 'BEGIN';
    end = 'END';
    new = 'NEW';
    return = 'RETURN';
    if = 'IF';
    then = 'THEN';
    else = 'ELSE';
    while = 'WHILE';
    for = 'FOR';
    bool = 'BOOLEAN';
    true = 'TRUE';
    false = 'FALSE';
    string = 'STRING';
    int = 'INT';
    void = 'VOID';
    real = 'REAL';
    switch = 'SWITCH';
    case = 'CASE';
    break = 'BREAK';
    default = 'DEFAULT';
    get = 'GET';
    put = 'PUT';
    plus = '+';
    inc = '++';
    minus = '-';
    dec = '--';
    times = '*';
    lparen = '(';
    rparen = ')';
    lbracket = '[';
    rbracket = ']';
    lcurly = '{';
    rcurly = '}';
    lt = '<';
    gt = '>';
    lte = '<=';
    gte = '>=';
    comma = ',';
    quote = '"';
    colon = ':';
    walrus = ':=';
    not = '!';
    and = '&&';
    semicolon = ';';
    equal = '=';
    eqv = '==';
    neqv = '!=';
    id = letter ( letter | digit | '_' )*;
    whitespace = (lf | sp | tabs);
    anychars = [35..255]+;
    integer = digit ( digit )*;
    real_num = ( digit )+ '.' ( digit )+;
    addop = '+' | '-';
    multop = '*' | '/';
    dot = '.';

Ignored Tokens
    whitespace;

Productions
    prog = begin classmethodstmts end;
    classmethodstmts = {recursive} classmethodstmts classmethodstmt
        | {empty}
        ;
    classmethodstmt = {classs} classs id lcurly methodstmtseqs rcurly
        | {methodstmtseq} methodstmtseq
        ;
    commaidstar = {comma_id} comma id
        | {empty}
        ;
    methodstmtseqs = {recursive} methodstmtseqs methodstmtseq
        | {empty}
        ;
    methodstmtseq = {method} type id lparen varlist rparen lcurly stmtseq rcurly
        | {statement} id commaidstar colon type semicolon
        ;
    stmtseq = {recursive} stmt stmtseq
        | {empty}
        ;
    stmt = {id} id optintbrack idintq
        |  {id_comma_int} id commaint* colon type int?
        |  {if} if lparen boolean rparen then lcurly stmtseq rcurly posselse
        ;
    optintbrack = {full} lbracket int rbracket
        | {empty}
        ;
    idintq = {number} walrus expr semicolon
        | {string} walrus [left]:quote anychars [right]:quote semicolon
        | {get} walrus get lparen rparen semicolon
        | {new} walrus new id lparen rparen semicolon
        | {dot} dot id lparen varlisttwo rparen idvarlisttwostar semicolon
        | {inc} inc
        | {dec} dec
        ;
    commaint = comma int;
    posselse = {else} else rcurly stmtseq lcurly
        | {empty}
        ;
    idvarlisttwostar = dot;
    expr = dot;
    varlist = {full} id colon type optintbrack commaidtypestar
        | {empty}
        ;
    commaidtypestar = {recursive} comma id colon type optintbrack commaidtypestar
        | {empty}
        ;
    varlisttwo = dot;
    boolean = {true} true
        | {false} false
        | {expr} [firstexpr]:expr cond [secondexpr]:expr
        | {id} id
        ;
    cond = {eqv} eqv 
        | {neqv} neqv
        | {gte} gte 
        | {lte} lte
        | {gt} gt 
        | {lt} lt
        ;
    type = {int} int
        | {real} real
        | {string} string
        | {bool} bool
        | {void} void
        | {id} id
        ;
