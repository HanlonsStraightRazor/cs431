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
    integer = digit+;
    float = digit+ '.' digit+;
    dot = '.';

Ignored Tokens
    whitespace;

Productions
    prog = begin classmethodstmts end;
    classmethodstmts = {rec} classmethodstmts classmethodstmt
        | {empty};
    classmethodstmt = {classs} classs id lcurly methodstmtseqs rcurly
        | {methodstmtseq} methodstmtseq;
    commaidstar = {commaid} comma id
        | {empty};
    methodstmtseqs = {rec} methodstmtseqs methodstmtseq
        | {empty};
    methodstmtseq = {method} type id lparen varlist rparen lcurly stmtseq rcurly
        | {statement} id commaidstar colon type semicolon;
    stmtseq = {rec} stmtseq stmt
        | {empty};
    stmt = {idoptintbrack} id optintbrack idintq semicolon
        | {idcommaidstar} id commaidstar colon type optintbrack semicolon
        | {if} if lparen boolean rparen then lcurly stmtseq rcurly optelsestmt
        | {while} while lparen boolean rparen lcurly stmtseq rcurly
        | {for} for lparen type? id walrus expr [left]:semicolon boolean [right]:semicolon incdecexpr rparen lcurly stmtseq rcurly
        | {put} put lparen id optintbrack rparen semicolon
        | {varlist} id lparen varlisttwo rparen semicolon
        | {return} return expr semicolon
        | {switch} switch [leftlparen]:lparen expr [leftrparen]:rparen lcurly case [rightlparen]:lparen integer [rightrparen]:rparen [leftcolon]:colon stmtseq optbreaksemi endcase rcurly;
    optbreaksemi = {full} break semicolon
        | {empty};
    endcase = casebreakstar default colon stmtseq;
    casebreakstar = {rec} case lparen integer rparen colon stmtseq optbreaksemi casebreakstar
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
    idvarlisttwostar = {rec} idvarlisttwostar dot id lparen varlisttwo rparen
        | {empty};
    expr = {add} expr plus term
        | {sub} expr minus term
        | {term} term;
    term = {mul} term times factor
        | {div} term divide factor
        | {factor} factor;
    varlist = {full} id colon type optintbrack commaidtypestar
        | {empty};
    commaidtypestar = {rec} comma id colon type optintbrack commaidtypestar
        | {empty};
    varlisttwo = {full} expr commaexprstar
        | {empty};
    commaexprstar = {rec} commaexprstar comma expr
        | {empty};
    boolean = {true} true
        | {false} false
        | {expr} [left]:expr cond [right]:expr /* WATCH: May be a problem later... */
        | {id} id;
    cond = {eqv} eqv
        | {neqv} neqv
        | {gte} gte
        | {lte} lte
        | {gt} gt
        | {lt} lt;
    type = {int} int
        | {real} real
        | {string} string
        | {bool} bool
        | {void} void
        | {id} id;
    /* FIXME */
    factor = {lparen} lparen expr rparen /* <- shift/reduce conflict with commaexprstar on rparen */
        | {minus} minus factor
        | {integer} integer
        | {float} float
        | {bool} bool
        /* | {id} id factorid <- major reduce/reduce conflicts */
        | {varlist} lparen varlisttwo rparen;
    factorid = {optintbrack} optintbrack factoroptintbrack
        | {empty};
    factoroptintbrack = {dot} dot id lparen varlisttwo rparen
        | {empty};
