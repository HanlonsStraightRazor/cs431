Package ParserProject;

Helpers
    sp  = ' ' | 10;
    number = ['0'..'9'];
    letter = ['a'..'z'] | ['A'..'Z'];
    alphanumeric = letter | number;
    
Tokens
    id = letter (letter | number)*;
    digit = number+;
    whitespace = sp+;
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
    ClassMethodStmts = ClassMethodStmts ClassMethodStmt |lambda;
    ClassMethodStmt = CLASS Id{MethodStmtSeqs}| Type Id(VarList){StmtSeq}|Id(,Id)∗:Type Semi;
    MethodStmtSeqs = MethodStmtSeqs MethodStmtSeq|lambda;
    MethodStmtSeq = Type Id(VarList){StmtSeq}|Id(,Id)∗:Type Semi;
    StmtSeq = Stmt StmtSeq|lambda;
    Stmt = Id( [Int] )? :=Expr Semi|Id( [Int] )? := “AnyChars” Semi|Id(,Id)∗:Type( [Int] )? Semi|IF (Boolean) THEN{StmtSeq}|IF (Boolean) 
        THEN{StmtSeq}ELSE{StmtSeq}|WHILE (Boolean){StmtSeq}|FOR ( (Type)?Id:=Expr Semi Boolean Semi (Id++|Id−− |Id:=Expr) ){StmtSeq}|
        Id( [Int] )? := GET() Semi |PUT (Id( [Int] )? ) Semi |Id( [Int] )?++ Semi |Id( [Int] )?−− Semi |Id( [Int] )? := NEW Id() Semi |
        Id(VarListTwo) Semi |Id( [Int] )? .Id (VarListTwo) ( .Id (VarListTwo) )∗ Semi | RETURN Expr Semi 1
        |Id( [Int] )? :=Boolean Semi |SWITCH (Expr){CASE(Int) :StmtSeq(BREAK Semi)? ( CASE(Int) :StmtSeq(BREAK Semi)?)∗DEFAULT :StmtSeq};
    VarList = (Id:Type( [Int] )? (,Id:Type( [Int] )? )∗)?;
    VarListTwo = (Expr(,Expr)∗)?;
    Expr = Expr AddOp Term|Term;
    Term = Term MultOp Factor|Factor;
    Factor = (Expr)|-Factor|Int|Real|Boolean|Id( [Int] )?|Id(VarListTwo)|Id( [Int] )? .Id(VarListTwo);
    Boolean = TRUE|FALSE|Expr Cond Expr|Id;
    Cond = ==|!=|>=|<=|>|<;
    AddOp = +|-;
    MultOp = *|/;
    Type = INT|REAL|STRING|BOOLEAN|VOID|Id;
    Id = Letter(Letter|Digit|)∗2;
    Int = Digit(Digit)∗;
    Real = (Digit)+.(Digit)+;
    AnyChars = [35..255]+;
    Letter = [‘a’..‘z’]|[‘A’..‘Z’];
    Digit = [‘0’..‘9’];
    Semi = ';';