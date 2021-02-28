Package Project1;

Helpers
    letter = ['A'..'Z'] | ['a'..'z'];
    digit = ['0'..'9'];
    lf = 10 | 13;
    sp = ' ';
    any = [0..25];
Tokens
    whitespace = (lf | sp);
    TNumber = (integer_literal)+ '.' (integer_literal)*;
    integer_literal = digit+;
    TComments = ('//' any* lf) | ('/*' any* '*/');
    TID = letter (digit | letter | '_')*;
    TClass = 'class';
    TPublic = 'public';
    TStatic = 'static';
    TVoid = 'void';
    TMain = 'main';
    TThis = 'this';
    TReturn = 'return';
    TIf = 'if';
    TElse = 'else';
    TAdd = '+';
    TMinus = '-';
    TTimes = '*';
    TLparen = '(';
    TRparen = ')';
    TLbracket = '[';
    TRbracket = ']';
    TLcurly = '{';
    TRcurly = '}';
    TPrint = 'System.out.println';
    TNew = 'new';
    TPeriod = '.';
    TSemicolon = ';';
    TEqual = '=';