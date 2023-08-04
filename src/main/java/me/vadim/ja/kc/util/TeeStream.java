//@formatter:off
package me.vadim.ja.kc.util;

import java.io.PrintStream;

/**
 * @author KCD
 * @link https://stackoverflow.com/a/10288881/12344841
 */
public class TeeStream extends PrintStream {
    PrintStream out;
    public TeeStream(PrintStream out1, PrintStream out2) {
        super(out1);
        this.out = out2;
    }
    public void write(byte buf[], int off, int len) {
        try {
            super.write(buf, off, len);
            out.write(buf, off, len);
        } catch (Exception e) {
        }
    }
    public void flush() {
        super.flush();
        out.flush();
    }
}