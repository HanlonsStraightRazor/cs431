package FinalProject;

class MIPS {
    private static final String DELIMITER = "    ";
    private static final String STRINGPREFIX = "string";
    private static final String WORDPREFIX = "word";
    private static final String LABELPREFIX = "label";
    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";
    private StringBuilder strings;
    private StringBuilder words;
    private StringBuilder main;
    private StringBuilder text;
    private int stringnum;
    private int wordnum;
    private int labelnum;
    private boolean isMain;
    private boolean errors;

    public MIPS() {
        strings = new StringBuilder();
        words = new StringBuilder();
        main = new StringBuilder();
        text = new StringBuilder();
        stringnum = 0;
        wordnum = 0;
        labelnum = 0;
        isMain = false;
        errors = false;

        strings.append(
            String.format(
                "%s.data\n",
                DELIMITER
            ) +
            String.format(
                "TRUE:\n%s.asciiz \"%s\"\n",
                DELIMITER,
                TRUE
            ) +
            String.format(
                "FALSE:\n%s.asciiz \"%s\"\n",
                DELIMITER,
                FALSE
            )
        );
        main.append(
            String.format(
                "\n%s.text\nmain:\n",
                DELIMITER
            )
        );
    }

    public String getCode() {
        if (errors) {
            return null;
        }
        return strings.toString() + words.toString() + main.toString() + text.toString();
    }

    public void printError(String err) {
        System.err.println(err);
        errors = true;
    }

    public void setMain(boolean b) {
        isMain = b;
    }

    public String addString(String s) {
        String label = String.format(
            "%s%d",
            STRINGPREFIX,
            stringnum++
        );
        strings.append(
            String.format(
                "%s:\n%s.asciiz %s\n",
                label,
                DELIMITER,
                s
            )
        );
        return label;
    }

    public String addWords(int num) {
        String label = String.format(
            "%s%d",
            WORDPREFIX,
            wordnum++
        );
        words.append(
            String.format(
                "%s:\n%s.word %d\n",
                label,
                DELIMITER,
                num
            )
        );
        return label;
    }

    private void append(String s) {
        if (isMain) {
            main.append(s);
        } else {
            text.append(s);
        }
    }

    public String getLabel() {
        return LABELPREFIX + labelnum;
    }

    public void incLabel() {
        labelnum++;
    }

    public void decLabel() {
        labelnum--;
    }

    public void addLabel(String label) {
        append(
            String.format(
                "%s:\n",
                label
            )
        );
    }

    public String addLabel() {
        String label = String.format(
            "%s%d",
            LABELPREFIX,
            labelnum++
        );
        append(
            String.format(
                "%s:\n",
                label
            )
        );
        return label;
    }

    public void add(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%sadd %s, %s, %s\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void addi(String dest, String src, int num) {
        append(
            String.format(
                "%saddi %s, %s, %d\n",
                DELIMITER,
                dest,
                src,
                num
            )
        );
    }

    public void jr(String reg) {
        append(
            String.format(
                "%sjr %s\n",
                DELIMITER,
                reg
            )
        );
    }

    public void la(String dest, String label) {
        append(
            String.format(
                "%sla %s, %s\n",
                DELIMITER,
                dest,
                label
            )
        );
    }

    public void lw(String dest, int offset, String src) {
        append(
            String.format(
                "%slw %s, %d(%s)\n",
                DELIMITER,
                dest,
                offset,
                src
            )
        );
    }

    public void l_s(String dest, int offset, String src) {
        append(
            String.format(
                "%sl.s %s, %d(%s)\n",
                DELIMITER,
                dest,
                offset,
                src
            )
        );
    }

    public void sw(String src, int offset, String dest) {
        append(
            String.format(
                "%ssw %s, %d(%s)\n",
                DELIMITER,
                src,
                offset,
                dest
            )
        );
    }

    public void s_s(String src, int offset, String dest) {
        append(
            String.format(
                "%ss.s %s, %d(%s)\n",
                DELIMITER,
                src,
                offset,
                dest
            )
        );
    }

    public void beq(String lhs, String rhs, String label) {
        append(
            String.format(
                "%sbeq %s, %s, %s\n",
                DELIMITER,
                lhs,
                rhs,
                label
            )
        );
    }

    public void j(String label) {
        append(
            String.format(
                "%sj %s\n",
                DELIMITER,
                label
            )
        );
    }

    public void bne(String lhs, String rhs, String label) {
        append(
            String.format(
                "%sbne %s, %s, %s\n",
                DELIMITER,
                lhs,
                rhs,
                label
            )
        );
    }

    public void li(String dest, int num) {
        append(
            String.format(
                "%sli %s, %d\n",
                DELIMITER,
                dest,
                num
            )
        );
    }

    public void syscall() {
        append(
            String.format(
                "%ssyscall\n",
                DELIMITER
            )
        );
    }

    public void swc1(String src, int offset, String dest) {
        append(
            String.format(
                "%sswc1 %s, %d(%s)\n",
                DELIMITER,
                src,
                offset,
                dest
            )
        );
    }

    public void lwc1(String dest, int offset, String src) {
        append(
            String.format(
                "%slwc1 %s, %d(%s)\n",
                DELIMITER,
                dest,
                offset,
                src
            )
        );
    }

    public void move(String dest, String src) {
        append(
            String.format(
                "%smove %s, %s\n",
                DELIMITER,
                dest,
                src
            )
        );
    }

    public void mtc1(String src, String dest) {
        append(
            String.format(
                "%smtc1 %s, %s\n",
                DELIMITER,
                src,
                dest
            )
        );
    }

    public void mfc1(String src, String dest) {
        append(
            String.format(
                "%smfc1 %s, %s\n",
                DELIMITER,
                src,
                dest
            )
        );
    }

    public void cvt_s_w(String dest, String src) {
        append(
            String.format(
                "%scvt.s.w %s, %s\n",
                DELIMITER,
                dest,
                src
            )
        );
    }

    public void cvt_w_s(String dest, String src) {
        append(
            String.format(
                "%scvt.w.s %s, %s\n",
                DELIMITER,
                dest,
                src
            )
        );
    }

    public void add_s(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%sadd.s %s, %s, %s\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void sub(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%ssub %s, %s, %s,\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void sub_s(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%ssub.s %s, %s, %s,\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void mult(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%smult %s, %s, %s,\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void mul_s(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%smul.s %s, %s, %s,\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void mflo(String dest) {
        append(
            String.format(
                "%smflo %s\n",
                DELIMITER,
                dest
            )
        );
    }

    public void div(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%sdiv %s, %s, %s,\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void div_s(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%sdiv.s %s, %s, %s,\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void c_eq_s(String lhs, String rhs) {
        append(
            String.format(
                "%sc.eq.s %s, %s\n",
                DELIMITER,
                lhs,
                rhs
            )
        );
    }

    public void c_ne_s(String lhs, String rhs) {
        append(
            String.format(
                "%sc.ne.s %s, %s\n",
                DELIMITER,
                lhs,
                rhs
            )
        );
    }

    public void c_lt_s(String lhs, String rhs) {
        append(
            String.format(
                "%sc.lt.s %s, %s\n",
                DELIMITER,
                lhs,
                rhs
            )
        );
    }

    public void c_le_s(String lhs, String rhs) {
        append(
            String.format(
                "%sc.le.s %s, %s\n",
                DELIMITER,
                lhs,
                rhs
            )
        );
    }

    public void movf(String dest, String src) {
        append(
            String.format(
                "%smovf %s, %s\n",
                DELIMITER,
                dest,
                src
            )
        );
    }

    public void seq(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%sseq %s, %s, %s\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void sne(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%ssne %s, %s, %s\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }


    public void sgt(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%ssgt %s, %s, %s\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void slt(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%sslt %s, %s, %s\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void sge(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%ssge %s, %s, %s\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }

    public void sle(String dest, String lhs, String rhs) {
        append(
            String.format(
                "%ssle %s, %s, %s\n",
                DELIMITER,
                dest,
                lhs,
                rhs
            )
        );
    }
}
