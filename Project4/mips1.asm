    .data

TRUE:
    .asciiz "TRUE"
FALSE:
    .asciiz "FALSE"

    .text

    li $t0, 2
    mtc1 $t0, $f0
    cvt.s.w $f0, $f0
    swc1 $f0, -4($sp)
    li $t1, 1082549862
    mtc1 $t1, $f0
    swc1 $f0, -0($sp)
    lwc1 $f0, -0($sp)
    s.s $f0, -12($sp)
    lwc1 $f0, -4($sp)
    l.s $f1, -12($sp)
    mul.s $f0, $f1, $f0
    swc1 $f0, -8($sp)
    li $v0, 10
    syscall
