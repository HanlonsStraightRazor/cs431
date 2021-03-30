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
    minus = '-';
    times = '*';
    plusOne = '++';
    minusOne = '--';
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
    walrus = ':='
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
    cond = '==' |
        '!=' |
        '>=' |
        '<=' |
        '>' |
        '<';
    addop = '+' |
        '-';
    multop = '*' |
        '/';

Ignored Tokens
    whitespace;

Productions
    prog = begin classmethodstmts end;
    classmethodstmts = classmethodstmts classmethodstmt
        | ;
    classmethodstmt = class id lcurlymethodstmtseqsrcurly
        | type idlparenvarlistrparenlcurlystmtseqrcurly
        | idlparen comma idrparen*:type semicolon;
    methodstmtseqs = methodstmtseqs methodstmtseq
        | ;
    methodstmtseq = type id lparen varlist rparen lcurly stmtseq rcurly
        | id lparen comma id rparen * : type semicolon;
    stmtseq = stmt stmtseq
        | ;
    stmt = id lparen [ int ] rparen ? : = expr semicolon
        | id lparen [ int ] rparen ? : = “anychars” semicolon
        | id lparen comma id rparen * : type lparen [ int ] rparen? semicolon
        | if lparen boolean rparen then lcurly stmtseq rcurly
        | if lparen boolean rparen then lcurly stmtseq rcurly else lcurly stmtseq rcurly
        | while lparen boolean rparen lcurly stmtseq rcurly
        | for lparen lparen type rparen? id := expr semicolon boolean semi lparenid++
        | id−−
        | id := expr rparen rparen lcurly stmtseq rcurly
        | id lparen [ int ] rparen? := getlparenrparen semicolon
        | put lparen idlparen [ int ] rparen? rparen semicolon
        | id lparen [ int ] rparen?++ semicolon
        | id lparen [ int ] rparen?−− semicolon
        | id lparen [ int ] rparen? := new idlparenrparen semi
        | id lparen varlisttwo rparen semicolon
        | id lparen [ int ] rparen? .id lparenvarlisttworparen lparen .id lparenvarlisttworparen rparen∗ semicolon
        | return expr semicolon
        | id lparen [ int ] rparen? := boolean semicolon
        | switch lparen expr rparen lcurly case lparen int rparen : stmtseq lparenbreak semicolonrparen? lparen case lparen int rparen : stmtseq lparen break semicolon rparen? rparendefault : stmtseq rcurly;
    varlist = lparen id:type lparen [ int ] rparen? lparen comma id : type lparen [ int ] rparen? rparen∗rparen?;
    varlisttwo = lparen expr lparen comma expr rparen∗rparen?;
    expr = expr addop term
        | term;
    term = term multop factor
        | factor;
    factor = lparen expr rparen
        | -factor
        | int
        | real
        | boolean
        | idlparen [int] rparen?
        | idlparen varlisttwo rparen
        | idlparen [ int ] rparen? .id lparenvarlisttworparen;
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
