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
    prog =
        begin classmethods end
        ;
    classmethods = classmethod*
        ;
    classmethod =
        {classs} classs id lcurly methodstmts rcurly
        | {methodstmt} methodstmt
        ;
    methodstmts =
        methodstmt*
        ;
    methodstmt =
        {method} type id lparen varlist? rparen lcurly stmt* rcurly
        | {statement} id commaid* colon type semicolon
        ;
    stmt =
        {idintbrack} id intbrack? idintq semicolon
        | {idcommaid} id commaid colon type intbrack? semicolon
        | {if} if lparen boolean rparen then lcurly stmt* rcurly elsestmt?
        | {while} while lparen boolean rparen lcurly stmt* rcurly
        | {for} for lparen type? id walrus expr [left]:semicolon boolean [right]:semicolon incdecexpr rparen lcurly stmt* rcurly
        | {put} put lparen id intbrack? rparen semicolon
        | {varlist} id lparen varlisttwo? rparen semicolon
        | {return} return expr semicolon
        | {switch} switch [leftlp]:lparen expr [leftrp]:rparen lcurly case [rightlp]:lparen integer [rightrp]:rparen colon stmt* breaksemi? endcase rcurly
        ;
    commaid =
        comma id
        ;
    breaksemi =
        break semicolon
        ;
    endcase =
        casebreak* default colon stmt*
        ;
    casebreak =
        case lparen integer rparen colon stmt* breaksemi?
        ;
    incdecexpr =
        {inc} id inc
        | {dec} id dec
        | {walrus} id walrus expr
        ;
    intbrack =
        lbracket int rbracket
        ;
    idintq =
        {number} walrus expr
        | {boolean} walrus boolean
        | {string} walrus [left]:quote anychars [right]:quote
        | {get} walrus get lparen rparen
        | {new} walrus new id lparen rparen
        | {dot} dot id lparen varlisttwo? rparen idvarlisttwo*
        | {inc} inc
        | {dec} dec
        ;
    elsestmt =
        else lcurly stmt* rcurly
        ;
    idvarlisttwo =
        dot id lparen varlisttwo? rparen
        ;
    expr =
        {add} expr plus term
        | {sub} expr minus term
        | {term} term
        ;
    term =
        {mul} term times factor
        | {div} term divide factor
        | {factor} factor
        ;
    varlist =
        id colon type intbrack? commaidtype
        ;
    commaidtype =
        comma id colon type intbrack?
        ;
    varlisttwo =
        expr commaexpr*
        ;
    commaexpr =
        comma expr
        ;
    boolean =
        {true} true
        | {false} false
        | {expr} [left]:expr cond [right]:expr
        | {id} id
        ;
    cond =
        {eqv} eqv
        | {neqv} neqv
        | {gte} gte
        | {lte} lte
        | {gt} gt
        | {lt} lt
        ;
    type =
        {int} int
        | {real} real
        | {string} string
        | {bool} bool
        | {void} void
        | {id} id
        ;
    factor =
        {lparen} lparen expr rparen
        | {minus} minus factor
        | {integer} integer
        | {float} float
        | {bool} bool
        | {id} id factorid?
        | {varlist} lparen varlisttwo? rparen
        ;
    factorid =
        intbrack? factorintbrack?
        ;
    factorintbrack =
        dot id lparen varlisttwo? rparen
        ;
