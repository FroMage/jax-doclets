package com.lunatech.doclets.jax.jaxb;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.lunatech.doclets.jax.jaxb.testcase.ClassExample;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javadoc.JavadocTool;
import com.sun.tools.javadoc.Messager;
import com.sun.tools.javadoc.ModifierFilter;
import com.sun.tools.javadoc.RootDocImpl;

public class JAXBDocletTest {

  private static final Logger logger = Logger.getLogger(JAXBDocletTest.class
      .getName());

  @Test
  public void testStart() throws IOException {
    Context context = new Context();
    Options compOpts = Options.instance(context);
    compOpts.put("-sourcepath", "src/test/java");
    new PublicMessager(context, "test", new PrintWriter(new LogWriter(
        Level.SEVERE), true), new PrintWriter(new LogWriter(Level.WARNING),
        true), new PrintWriter(new LogWriter(Level.FINE), true));

    JavadocTool javadocTool = JavadocTool.make0(context);
    ListBuffer<String> javaNames = new ListBuffer<String>();
    javaNames.append(ClassExample.class.getPackage().getName());
    ListBuffer<String[]> options = new ListBuffer<String[]>();
    ListBuffer<String> packageNames = new ListBuffer<String>();
    ListBuffer<String> excludedPackages = new ListBuffer<String>();

    RootDocImpl rootDocImpl = javadocTool.getRootDocImpl("en", "",
        new ModifierFilter(ModifierFilter.ALL_ACCESS), javaNames.toList(),
        options.toList(), false, packageNames.toList(),
        excludedPackages.toList(), false, false, false);
    logger.info(rootDocImpl.getRawCommentText());
    JAXBDoclet.start(rootDocImpl);
  }

  protected class LogWriter extends Writer {

    Level level;

    public LogWriter(Level level) {
      this.level = level;
    }

    public void write(char[] chars, int offset, int length) throws IOException {
      String s = new String(Arrays.copyOf(chars, length));
      if (!s.equals("\n"))
        logger.log(level, s);
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
    }
  }

  public class PublicMessager extends Messager {

    public PublicMessager(Context context, String s) {
      super(context, s);
    }

    public PublicMessager(Context context, String s, PrintWriter printWriter,
        PrintWriter printWriter1, PrintWriter printWriter2) {
      super(context, s, printWriter, printWriter1, printWriter2);
    }
  }

}
