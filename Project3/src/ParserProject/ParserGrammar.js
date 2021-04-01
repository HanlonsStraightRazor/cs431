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
    divide = '/';
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
    whole_num = digit+;
    real_num = digit+ '.' digit+;
    dot = '.';

Ignored Tokens
    whitespace;

Productions
    prog = begin classmethodstmts end;
    classmethodstmts = {recursive} classmethodstmts classmethodstmt
        | {empty};
    classmethodstmt = {classs} classs id lcurly methodstmtseqs rcurly
        | {methodstmtseq} methodstmtseq;
    commaidstar = {commaid} comma id
        | {empty};
    methodstmtseqs = {recursive} methodstmtseqs methodstmtseq
        | {empty};
    methodstmtseq = {method} type id lparen varlist rparen lcurly stmtseq rcurly
        | {statement} id commaidstar colon type semicolon;
    stmtseq = {recursive} stmt stmtseq
        | {empty};
    stmt = {idoptintbrack} id optintbrack idintq semicolon
        | {idcommaidstar} id commaidstar colon type optintbrack semicolon
        | {if} if lparen boolean rparen then lcurly stmtseq rcurly optelsestmt
        | {while} while lparen boolean rparen lcurly stmtseq rcurly
        | {for} for lparen type? id walrus expr [left]:semicolon boolean [right]:semicolon incdecexpr rparen lcurly stmtseq rcurly
        | {put} put lparen id optintbrack rparen semicolon
        | {varlist} id lparen varlisttwo rparen semicolon
        | {return} return expr semicolon
        | {switch} switch [leftlparen]:lparen expr [leftrparen]:rparen lcurly case [rightlparen]:lparen integer [rightrparen]:rparen [leftcolon]:colon [left]:stmtseq optbreaksemi casebreakstar default [rightcolon]:colon [right]:stmtseq rcurly;
    optbreaksemi = {full} break semicolon
        | {empty};
    casebreakstar = {recursive} case lparen integer rparen colon stmtseq optbreaksemi casebreakstar
        | {empty};
    incdecexpr = {inc} id inc
        | {dec} id dec
        | {walrus} id walrus expr;
    optintbrack = {full} lbracket int rbracket
        | {empty};
    idintq = {number} walrus expr
        | {boolean} walrus boolean
        | {string} walrus [left]:quote anychars [right]:quote
        | {get} walrus get lparen rparen
        | {new} walrus new id lparen rparen
        | {dot} dot id lparen varlisttwo rparen idvarlisttwostar
        | {inc} inc
        | {dec} dec;
    optelsestmt = {full} else lcurly stmtseq rcurly
        | {empty};
    idvarlisttwostar = dot;
    expr = {addop} expr addop term
        | {term} term;
    term = {multop} term multop factor
        | {factor} factor;
    varlist = {full} id colon type optintbrack commaidtypestar
        | {empty};
    commaidtypestar = {recursive} comma id colon type optintbrack commaidtypestar
        | {empty};
    varlisttwo = {full} expr commaexprstar
        | {empty};
    commaexprstar = {recursive} comma expr commaexprstar
        | {empty};
    boolean = {true} true
        | {false} false
        | {expr} [left]:expr cond [right]:expr
        | {id} id;
    cond = {eqv} eqv
        | {neqv} neqv
        | {gte} gte
        | {lte} lte
        | {gt} gt
        | {lt} lt;
    addop = {plus} plus 
        | {minus} minus;
    multop = {times} times 
        | {divide} divide;
    type = {int} int
        | {real} real
        | {string} string
        | {bool} bool
        | {void} void
        | {id} id;
    factor = {lparen} lparen expr rparen
        | {id} id factorid
        | {integer} integer
        | {real} real
        | {bool} bool
        | {minus} minus factor;
    factorid = {optintbrack} optintbrack factoroptintbrack
        | {lparen} lparen varlisttwo rparen;
    factoroptintbrack = {dot} dot id lparen varlisttwo rparen
        | {empty};
    integer = whole_num+;
