Package FinalProject;

Helpers
    tab = 9;
    line_feed = 10;
    carriage_return= 13;
    quote = 34;
    anything = [35..255];
    digit = ['0'..'9'];
    letter = ['a'..'z'] | ['A'..'Z'];

    number = digit+;
    space = ' ';

    end_of_line = line_feed | carriage_return | carriage_return line_feed; //newline depends on windows vs linux

Tokens
    plusop = '+';
    negop =  '-';
    multop = '*' | '/';
    cond = '==' | '!=' | '>' | '<' | '>=' | '<=';

    begin = 'BEGIN';
    break = 'BREAK';
    case = 'CASE';
    class_lit = 'CLASS';
    default = 'DEFAULT';
    else = 'ELSE';
    end = 'END';
    false = 'FALSE';
    for = 'FOR';
    get = 'GET';
    if = 'IF';
    new = 'NEW';
    put = 'PUT';
    return = 'RETURN';
    switch = 'SWITCH';
    then = 'THEN';
    true = 'TRUE';
    while = 'WHILE';

    comma = ',';
    colon = ':';
    period = '.';
    semicolon = ';';
    equals = ':=';
    incr = '++';
    decr = '--';
    lparen = '(';
    rparen = ')';
    lbracket = '[';
    rbracket = ']';
    lcurly = '{';
    rcurly = '}';
    type_decl = 'INT' | 'REAL' | 'STRING'  | 'BOOLEAN' | 'VOID';
    id = letter (letter* | digit* | '_'*);

    real = number+ '.' number+;
    int = number number*;
    anychars = quote (anything | space | tab | end_of_line)+ quote;
    whitespace = (space | tab | end_of_line)+;

Ignored Tokens
    whitespace;

Productions
    prog = begin classmethodstmts end ;
    classmethodstmts = {class_stmts} classmethodstmts classmethodstmt
                    | {epsilon};
    classmethodstmt = {class_def} class_lit id lcurly methodstmtseqs rcurly
                    | {method_decl} type id lparen varlist rparen lcurly stmtseq rcurly
                    | {var_decl} id more_ids colon type semicolon;

    methodstmtseqs =  {method_stmts} methodstmtseqs methodstmtseq
                    |  {epsilon};

    methodstmtseq = {method_decl} type id lparen varlist rparen lcurly stmtseq rcurly
                    | {var_decl} id more_ids colon type semicolon
                    | {assign_equals} id array_option equals expr semicolon
                    | {assign_string} id array_option equals anychars semicolon
                    | {print_stmt} put lparen id array_option rparen semicolon
                    | {assign_read_in} id array_option equals get lparen rparen semicolon
                    | {assign_inc} id array_option incr semicolon
                    | {assign_dec} id array_option decr semicolon
                    | {decl_object} [left_side]:id array_option equals new [right_side]:id lparen rparen semicolon
                    | {assign_boolean} id array_option equals boolean semicolon;

    stmtseq = {first_stmt} stmt stmtseq
           | {epsilon};

    stmt = {assign_expr} id array_option equals expr semicolon
        | {assign_string} id array_option equals anychars semicolon
        | {var_decl} id more_ids colon type array_option semicolon
        | {if_block} if lparen boolid rparen then lcurly stmtseq rcurly
        | {if_else_block} if lparen boolid rparen then [iflcurly]:lcurly [if_block_stmts]:stmtseq [ifrcurly]:rcurly else [elselcurly]:lcurly [else_block_stmts]:stmtseq [elsercurly]:rcurly
        | {while} while lparen boolid rparen lcurly stmtseq rcurly
        | {for} for lparen for_optional_type id equals expr_or_bool [first]:semicolon boolid [second]:semicolon for_incr_step rparen lcurly stmtseq rcurly
        | {get} id array_option equals get lparen rparen semicolon
        | {put} put lparen id array_option rparen semicolon
        | {incr} id array_option incr semicolon
        | {decr} id array_option decr semicolon
        | {decl_object} [left_side]:id array_option equals new [right_side]:id lparen rparen semicolon
        | {method_call} id lparen var_list_two rparen semicolon
        | {method_call_in_class} [first_id]:id array_option period [instance_id]:id lparen var_list_two rparen method_chaining_option semicolon
        | {return} return expr_or_bool semicolon
        | {assign_boolean} id array_option equals boolean semicolon
        | {switch} switch [first]:lparen expr_or_bool [second]:rparen lcurly case [third]:lparen int [fourth]:rparen [fifth]:colon [stmts]:stmtseq break_helper case_helper default [seccolon]:colon [default_stmts]:stmtseq rcurly;

    case_helper = {another_case} case lparen int rparen colon stmtseq break_helper case_helper
                | {epsilon};

    break_helper = {break} break semicolon
                | {epsilon};

    method_chaining_option = {method_call} period id lparen var_list_two rparen method_chaining_option
                            | {epsilon};

    for_incr_step = {incr} id incr
                | {decr} id decr
                | {assignment} id equals expr_or_bool;

    for_optional_type = type
                  | {epsilon};

    more_ids = {more_ids} comma id more_ids
            | {epsilon};

    varlist = {more_ids} id colon type array_option more_varlist
           | {epsilon};

    array_option = {array} lbracket int rbracket
                | {epsilon};

    more_varlist = {more_ids} comma varlist
                | {epsilon};

    var_list_two = {var_list} expr_or_bool more_var_list_two
                | {epsilon};

    expr_or_bool = expr
                | {bool} boolean;

    more_var_list_two = comma var_list_two
                | {epsilon};

    expr = {add} expr addop term
        | {term} term;

    term = {mult} term multop factor
        | {factor} factor;

    factor = {expr} lparen expr rparen
            | {negative} negop factor
            | {int} int
            | {real} real
            | {array} array_or_id
            | {idvarlist} id lparen var_list_two rparen
            | {last} array_or_id period id lparen var_list_two rparen;

    array_or_id = {array} id lbracket int rbracket
                | {id} id;

    boolean = {true} true
            | {false} false
            | {conditional} [first]:expr cond [sec]:expr;

    boolid = {bool} boolean
            | {id} id;

    addop = {plus} plusop
        | {minus} negop;

    type = {types} type_decl
        | {id} id;
