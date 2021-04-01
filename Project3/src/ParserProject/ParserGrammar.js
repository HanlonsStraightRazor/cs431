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
    commaid = {commaid} comma id;
    methodstmtseqs = {rec} methodstmtseqs methodstmtseq
        | {empty};
    methodstmtseq = {method} type id lparen varlist rparen lcurly stmtseq rcurly
        | {statement} id commaid* colon type semicolon;
    stmtseq = {rec} stmtseq stmt
        | {empty};
    stmt = {idintbrack} id intbrack? idintq semicolon
        | {idcommaid} id commaid colon type intbrack? semicolon
        | {if} if lparen boolean rparen then lcurly stmtseq rcurly elsestmt?
        | {while} while lparen boolean rparen lcurly stmtseq rcurly
        | {for} for lparen type? id walrus expr [left]:semicolon boolean [right]:semicolon incdecexpr rparen lcurly stmtseq rcurly
        | {put} put lparen id intbrack? rparen semicolon
        | {varlist} id lparen varlisttwo rparen semicolon
        | {return} return expr semicolon
        | {switch} switch [leftlparen]:lparen expr [leftrparen]:rparen lcurly case [rightlparen]:lparen integer [rightrparen]:rparen colon stmtseq breaksemi? endcase rcurly;
    breaksemi = {full} break semicolon;
    endcase = casebreak* default colon stmtseq;
    casebreak = case lparen integer rparen colon stmtseq breaksemi?;
    incdecexpr = {inc} id inc
        | {dec} id dec
        | {walrus} id walrus expr;
    intbrack = {full} lbracket int rbracket
        | {empty};
    idintq = {number} walrus expr
        | {boolean} walrus boolean
        | {string} walrus [left]:quote anychars [right]:quote
        | {get} walrus get lparen rparen
        | {new} walrus new id lparen rparen
        | {dot} dot id lparen varlisttwo rparen idvarlisttwo*
        | {inc} inc
        | {dec} dec;
    elsestmt = {full} else lcurly stmtseq rcurly;
    idvarlisttwo = dot id lparen varlisttwo rparen;
    expr = {add} expr plus term
        | {sub} expr minus term
        | {term} term;
    term = {mul} term times factor
        | {div} term divide factor
        | {factor} factor;
    varlist = {full} id colon type intbrack? commaidtype
        | {empty};
    commaidtype = comma id colon type intbrack?;
    varlisttwo = {full} expr commaexpr*
        | {empty};
    commaexpr = comma expr;
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
    /* factorid = {intbrack} intbrack? factorintbrack?
        | {empty};
    factorintbrack = dot id lparen varlisttwo rparen; */
