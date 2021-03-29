Package ParserProject;

Helpers
    lf = 10 | 12 | 13;
    sp = ' ';
    tabs = 9 | 11;
    number = ['0'..'9'];
    letter = ['a'..'z'] | ['A'..'Z'];
    alphanumeric = letter | number;

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
    id = letter (letter | number)*;
    digit = number+;
    whitespace = (lf | sp | tabs);

Ignored Tokens
    whitespace;

Productions
    prog = {first} id digit |
           {second} lotnumbers |
           {third} [eachsymbolisuniqueinaproduction]:id [secondid]:id [digitone]:digit [digittwo]:digit ;
    lotnumbers = digit morenumbers;
    morenumbers = {fourth} digit morenumbers |
              {emptyproduction} ;




    Prog = BEGIN ClassMethodStmts END;
    ClassMethodStmts = ClassMethodStmts ClassMethodStmt |
        lambda;
    ClassMethodStmt = CLASS Id{MethodStmtSeqs}|
        Type Id(VarList){StmtSeq}|
        Id(,Id)∗:Type semicolon;
    MethodStmtSeqs = MethodStmtSeqs MethodStmtSeq|
        lambda;
    MethodStmtSeq = Type Id(VarList){StmtSeq}|
        Id(,Id)∗:Type semicolon;
    StmtSeq = Stmt StmtSeq|
        lambda;
    Stmt = Id( [Int] )? :=Expr semicolon|
        Id( [Int] )? := “AnyChars” semicolon|
        Id(,Id)∗:Type( [Int] )? semicolon|
        IF (Boolean) THEN{StmtSeq}|
        IF (Boolean) THEN{StmtSeq}ELSE{StmtSeq}|
        WHILE (Boolean){StmtSeq}|
        FOR ( (Type)?Id:=Expr semicolon Boolean Semi (Id++|Id−− |Id:=Expr) ){StmtSeq}|
        Id( [Int] )? := GET() semicolon |
        PUT (Id( [Int] )? ) semicolon |
        Id( [Int] )?++ semicolon |
        Id( [Int] )?−− semicolon |
        Id( [Int] )? := NEW Id() Semi |
        Id(VarListTwo) semicolon |
        Id( [Int] )? .Id (VarListTwo) ( .Id (VarListTwo) )∗ semicolon | RETURN Expr semicolon |
        Id( [Int] )? :=Boolean semicolon |
        SWITCH (Expr){CASE(Int) :StmtSeq(BREAK semicolon)? ( CASE(Int) :StmtSeq(BREAK semicolon)?)∗DEFAULT :StmtSeq};
    VarList = (Id:Type( [Int] )? (,Id:Type( [Int] )? )∗)?;
    VarListTwo = (Expr(,Expr)∗)?;
    Expr = Expr AddOp Term|
        Term;
    Term = Term MultOp Factor|
        Factor;
    Factor = (Expr)|
        -Factor|
        Int|
        Real|
        Boolean|
        Id( [Int] )?|
        Id(VarListTwo)|
        Id( [Int] )? .Id(VarListTwo);
    Boolean = TRUE|
        FALSE|
        Expr Cond Expr|
        Id;
    Cond = ==|
        !=|
        >=|
        <=|
        >|
        <;
    AddOp = +|
        -;
    MultOp = *|
        /;
    Type = INT|
        REAL|
        STRING|
        BOOLEAN|
        VOID|
        Id;
    Id = Letter(Letter|Digit|)∗2;
    Int = Digit(Digit)∗;
    Real = (Digit)+.(Digit)+;
    AnyChars = [35..255]+;
    Letter = [‘a’..‘z’]|[‘A’..‘Z’];
    Digit = [‘0’..‘9’];