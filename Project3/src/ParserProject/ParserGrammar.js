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
    not = '!';
    and = '&&';
    semicolon = ';';
    equal = '=';
    eq = '==';
    neq = '!=';
    id = letter ( letter | digit | _ )∗;
    whitespace = (lf | sp | tabs);
    anychars = [35..255]+;
    int = digit ( digit )∗;
    real = ( digit )+ .( digit )+;
    digit = [ '0'..'9' ];
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
    classmethodstmts = classmethodstmts classmethodstmt |
        lambda;
    classmethodstmt = class id {methodstmtseqs} |
        type id(varlist){stmtseq}|
        id(,id)∗:type semicolon;
    methodstmtseqs = methodstmtseqs methodstmtseq |
        lambda;
    methodstmtseq = type id ( varlist ) { stmtseq } |
        id ( , id ) ∗ : type semicolon;
    stmtseq = stmt stmtseq |
        lambda;
    stmt = id ( [ int ] ) ? : = expr semicolon |
        id ( [ int ] ) ? : = “anychars” semicolon |
        id ( , id ) ∗ : type ( [ int ] )? semicolon |
        if ( boolean ) then { stmtseq } |
        if ( boolean ) then { stmtseq } else { stmtseq } |
        while ( boolean ) { stmtseq } |
        for ( ( type )? id := expr semicolon boolean semi (id++ | id−− | id := expr ) ) { stmtseq } |
        id ( [ int ] )? := get() semicolon |
        put ( id( [ int ] )? ) semicolon |
        id ( [ int ] )?++ semicolon |
        id ( [ int ] )?−− semicolon |
        id ( [ int ] )? := new id() semi |
        id ( varlisttwo ) semicolon |
        id ( [ int ] )? .id (varlisttwo) ( .id (varlisttwo) )∗ semicolon | return expr semicolon |
        id ( [ int ] )? := boolean semicolon |
        switch ( expr ) { case ( int ) : stmtseq (break semicolon)? ( case ( int ) : stmtseq ( break semicolon )? )default : stmtseq };
    varlist = ( id:type ( [ int ] )? ( ,id : type ( [ int ] )? )∗)?;
    varlisttwo = ( expr ( ,expr )∗)?;
    expr = expr addop term |
        term;
    term = term multop factor |
        factor;
    factor = ( expr ) |
        -factor |
        int |
        real |
        boolean |
        id( [int] )? |
        id( varlisttwo ) |
        id( [ int ] )? .id (varlisttwo);
    boolean = true |
        false |
        expr cond expr |
        id;
    type = int |
        real |
        string |
        boolean |
        void |
        id;
    