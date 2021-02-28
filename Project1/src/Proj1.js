Package Project1;

Helpers
    letter = ['A'..'Z'] | ['a'..'z'];
    digit = ['0'..'9'];
    lf = 10 | 13;
    sp = ' ';
    any = [0..25];
Tokens
    if = 'if';
    whitespace = lf | sp;
    INTEGER_LITERAL = digit+;
    REAL_NUMBERS = (<INTEGER_LITERAL>)+ '.' (<INTEGER_LITERAL>)*;
    comment = ('//' any* lf) | ('/*' any* '*/');
    identifier = letter (digit | letter | '_')*;

    Goal =	MainClass ( ClassDeclaration )* <EOF>;
    MainClass = "class" Identifier "{" "public" "static" "void" "main" "(" "String" "[" "]" Identifier ")" "{" Statement "}" "}";
    ClassDeclaration = "class" Identifier ( "extends" Identifier )? "{" ( VarDeclaration )* ( MethodDeclaration )* "}";
    VarDeclaration = Type Identifier ";";
    MethodDeclaration = "public" Type Identifier "(" ( Type Identifier ( "," Type Identifier )* )? ")" "{" ( VarDeclaration )* ( Statement )* "return" Expression ";" "}";
    Type = "int" "[" "]"
        | "boolean"
        | "int"
        | Identifier;
    Statement = "{" ( Statement )* "}"
        | "if" "(" Expression ")" Statement "else" Statement
        | "while" "(" Expression ")" Statement
        | "System.out.println" "(" Expression ")" ";"
        | Identifier "=" Expression ";"
        |Identifier "[" Expression "]" "=" Expression ";";
    Expression = Expression ( "&&" | "<" | "+" | "-" | "*" ) Expression
        | Expression "[" Expression "]"
        | Expression "." "length"
        | Expression "." Identifier "(" ( Expression ( "," Expression )* )? ")"
        | <INTEGER_LITERAL>
        | "true"
        | "false"
        | Identifier
        | "this"
        | "new" "int" "[" Expression "]"
        | "new" Identifier "(" ")"
        | "!" Expression
        | "(" Expression ")";
    Identifier = <IDENTIFIER></IDENTIFIER>;