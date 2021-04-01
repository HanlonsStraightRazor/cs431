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
    cond = '==' | '!=' | '>=' | '<=' | '>' | '<';
    addop = '+' | '-';
    multop = '*' | '/';
    dot = '.';

Ignored Tokens
    whitespace;

Productions
    comma_id_star = {first} comma id comma_id_star
        | ;
    lbracket_integer_rbracket_question = {second} lbracket integer rbracket
        | ;
    type_question = {third} type
        | ;
    dot_id_lparen_varlisttwo_rparen_star = {fourth} dot id lparen varlisttwo rparen dot_id_lparen_varlisttwo_rparen_star
        | ;
    break_semicolon_question = {fifth} break semicolon
        | ;
    idinc_iddec_idwalrusexpr = {sixith} id inc
        | {seventh} id dec
        | {eighth} id walrus expr;
    case_lparen_integer_rparen_colon_stmtseq =  {ninth} case lparen integer rparen colon stmtseq case_lparen_integer_rparen_colon_stmtseq
        | ;
    comma_expr_star = {tenth} comma expr comma_expr_star
        | ;
    comma_id_colon_type_lbracketintegerrbracketquestion_star = {eleventh} comma id colon type lbracket_integer_rbracket_question comma_id_colon_type_lbracketintegerrbracketquestion_star
        | ;
    id_colon_type_lbracketintegerrbracketquestion_commaidcolontypelbracketintegerrbracketquestionstar_question = {tewelveth} id colon type lbracket_integer_rbracket_question comma_id_colon_type_lbracketintegerrbracketquestion_star
        | ;
    poss_else = {ninty} else lcurly stmtseq rcurly | ;

    prog = begin classmethodstmts end;
    classmethodstmts = {thirteenth} classmethodstmts classmethodstmt
        | ;
    classmethodstmt = {fourteenth} classs id lcurly methodstmtseqs rcurly
        | {fifthteenth} type id lparen varlist rparen lcurly stmtseq rcurly
        | {sixteenth} id comma_id_star colon type semicolon;
    methodstmtseqs = {seventeenth} methodstmtseqs methodstmtseq
        | ;
    methodstmtseq = {eighteenth} type id lparen varlist rparen lcurly stmtseq rcurly
        | {nineteenth} id comma_id_star colon type semicolon;
    stmtseq = {twentith} stmt stmtseq
        | ;
    stmt = {twentyfirst} id lbracket_integer_rbracket_question walrus expr semicolon
        | {twentysecond} id lbracket_integer_rbracket_question walrus lquote anychars rquote semicolon
        | {twentythird} id comma_id_star colon type lbracket_integer_rbracket_question semicolon
        | {twentyfourth} if lparen boolean rparen then lcurly stmtseq rcurly poss_else 
        | {twentysixth} while lparen boolean rparen lcurly stmtseq rcurly
        | {twentyseventh} for lparen type_question id walrus expr semicolon boolean semicolon idinc_iddec_idwalrusexpr rparen lcurly stmtseq rcurly
        | {twentyeighth} id lbracket_integer_rbracket_question walrus get lparen rparen semicolon
        | {twentyninth} put lparen id lbracket_integer_rbracket_question rparen semicolon
        | {thirtyth} id lbracket_integer_rbracket_question inc semicolon
        | {thirtyfirst} id lbracket_integer_rbracket_question dec semicolon
        | {thirtysecond} id lbracket_integer_rbracket_question walrus new id lparen rparen semicolon
        | {thirtythird} id lparen varlisttwo rparen semicolon
        | {thirtyfourth} id lbracket_integer_rbracket_question dot id lparen varlisttwo rparen dot_id_lparen_varlisttwo_rparen_star semicolon
        | {thirtyfifth} return expr semicolon
        | {thirtysixth} id lbracket_integer_rbracket_question walrus boolean semicolon
        | {thirtyseventh} switch lparen expr rparen lcurly case lparen integer rparen colon stmtseq break_semicolon_question case_lparen_integer_rparen_colon_stmtseq default colon stmtseq rcurly;
    varlist = id colon type lbracket_int_rbracket_question comma_id_colon_type_lbracketintrbracketquestion_star
        | ;
    varlisttwo = expr comma_expr_star
        | ;
    expr = {thirtyeighth} expr addop term
        | {thirtyninth} term;
    term = {fourtith} term multop factor
        | {fourtyfirst} factor;
    factor = {fourtysecond} lparen expr rparen
        | {fourtythird} minus factor
        | {fourtyfourth} integer
        | {fourtyfifth} real_num
        | {fourtysixth} boolean
        | {fourtyseventh} id lbracket_integer_rbracket_question
        | {fourtyeighth} id lparen varlisttwo rparen
        | {fourtyninth} id lbracket_integer_rbracket_question dot id lparen varlisttwo rparen;
    boolean = {fifty} true
        | {fiftyfirst} false
        | {fifthsecondth} expr cond expr
        | {fiftythird} id;
    type = {fiftyfourth} int
        | {fiftyfifth} real
        | {fiftysixth} string
        | {fiftyseventh} boolean
        | {fiftyeighth} void
        | {fiftyninth} id;
