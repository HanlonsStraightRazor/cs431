Package ParserProject;

Helpers
    lf = 10 | 12 | 13;
    sp = ' ';
    tabs = 9 | 11;
    digit = ['0'..'9'];
    letter = ['a'..'z'] | ['A'..'Z'];
    alphanumeric = letter | digit;

Tokens
    class = 'CLASS';
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
    int = digit ( digit )*;
    real = ( digit )+ '.' ( digit )+;
    cond = '==' | '!=' | '>=' | '<=' | '>' | '<';
    addop = '+' | '-';
    multop = '*' | '/';

Ignored Tokens
    whitespace;

Productions
    comma_id_star = comma id comma_id_star | ;
    lbracket_int_rbracket_question = lbracket int rbracket | ;
    type_question = type | ;
    idinc_iddec_idwalrusexpr = id inc | id dec | id walrus expr;

    prog = begin classmethodstmts end;
    classmethodstmts = classmethodstmts classmethodstmt
        | ;
    classmethodstmt = class id lcurly methodstmtseqs rcurly
        | type id lparen varlist rparen lcurly stmtseq rcurly
        | id commaid_star colon type semicolon;
    methodstmtseqs = methodstmtseqs methodstmtseq
        | ;
    methodstmtseq = type id lparen varlist rparen lcurly stmtseq rcurly
        | id commaid_star colon type semicolon;
    stmtseq = stmt stmtseq
        | ;
    stmt = id lbracket_int_rbracket_question walrus expr semicolon
        | id lbracket_int_rbracket_question walrus lquote anychars rquote semicolon
        | id comma_id_star colon type lbracket_int_rbracket_question semicolon
        | if lparen boolean rparen then lcurly stmtseq rcurly
        | if lparen boolean rparen then lcurly stmtseq rcurly else lcurly stmtseq rcurly
        | while lparen boolean rparen lcurly stmtseq rcurly
        | for lparen type_question id walrus expr semicolon boolean semicolon idinc_iddec_idwalrusexpr rparen lcurly stmtseq rcurly
        | id lbracket_int_rbracket_question walrus get lparen rparen semicolon
        | put lparen id lbracket_int_rbracket_question rparen semicolon
        | id lbracket_int_rbracket_question inc semicolon
        | id lbracket_int_rbracket_question dec semicolon
        | id lbracket_int_rbracket_question walrus new id lparenrparen semicolon
        | id lparen varlisttwo rparen semicolon
        | id lbracket_int_rbracket_question '.' id lparen varlisttwo rparen ( '.' id lparen varlisttwo rparen )* semicolon
        | return expr semicolon
        | id lbracket_int_rbracket_question walrus boolean semicolon
        | switch lparen expr rparen lcurly case lparen int rparen colon stmtseq ( break semicolon )? ( case lparen int rparen colon stmtseq ( break semicolon )?)* default colon stmtseq rcurly;
    varlist = ( id colon type lbracket_int_rbracket_question ( comma id colon type lbracket_int_rbracket_question )* )?;
    varlisttwo = ( expr ( comma expr )* )?;
    expr = expr addop term
        | term;
    term = term multop factor
        | factor;
    factor = lparen expr rparen
        | '-' factor
        | int
        | real
        | boolean
        | id lbracket_int_rbracket_question
        | id lparen varlisttwo rparen
        | id lbracket_int_rbracket_question '.' id lparen varlisttwo rparen;
    boolean = true
        | false
        | expr cond expr
        | id;
    type = int
        | real
        | string
        | boolean
        | void
        | id;
