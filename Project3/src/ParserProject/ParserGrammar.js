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
