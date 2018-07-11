package uk.ac.ed.inf.megamodelbuild.test.build;

import static uk.ac.ed.inf.megamodelbuild.test.build.BxExampleTest.Tool.*;
import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import build.pluto.BuildUnit;
import build.pluto.builder.BuildManagers;
import build.pluto.builder.BuildRequest;
import build.pluto.builder.Builder;
import build.pluto.builder.factory.BuilderFactory;
import build.pluto.output.Output;
import build.pluto.test.build.ScopedBuildTest;
import build.pluto.test.build.ScopedPath;
import build.pluto.util.IReporting;
import build.pluto.util.LogReporting;
import uk.ac.ed.inf.megamodelbuild.MegaBuilder.Input;
import uk.ac.ed.inf.megamodelbuild.bxexample.CodeBuilder;
import uk.ac.ed.inf.megamodelbuild.bxexample.ModelBuilder;
import uk.ac.ed.inf.megamodelbuild.bxexample.TestBuilder;

public class BxExampleTest extends ScopedBuildTest {

//by magic in ScopedBuildTest, this will end up pointing at the directory for each individual test
 @ScopedPath("")
 protected File dir;
 
 // and these at the local copies of the files we manipulate - may need if we want to compare file contents
// @ScopedPath("Code.java")
// private File codeFile;
//
// @ScopedPath("Model.xmi")
// private File modelFile;
// 
// @ScopedPath("Safety.txt")
// private File safetyFile;
// 
// @ScopedPath("Test.java")
// private File testFile;
// 
// @ScopedPath("orientation.txt")
// private File orientationFile;
// 
 protected static enum Tool {
   MODEL,
   CODE,
   TEST
 }
 
 // refactoring half way house
 protected Tool toTool(BuilderFactory<?, ?, ?> b) {
   if (b == ModelBuilder.factory) return MODEL;
   else if (b == CodeBuilder.factory) return CODE;
   else if (b == TestBuilder.factory) return TEST;
   else throw new Error("Unexpected factory "+b.toString());
 }
 
// created anew by each doBuild, examined by assertOrder. Will work just fine 
// till we start running tests in parallel and then break horribly, I fear...
 private List<BuilderFactory<?, ?, ?>> executed;
 
  // Pre: there must always be a doBuild before an assertOrder, so executed will not be null.
  // Check that the order in which the builders were executed in response to that doBuild is as specified in order
  protected void assertOrder(Tool... order) {
    Tool[] executedTools = new Tool[executed.size()];
    for (int i = 0; i < executedTools.length; i++) {
        executedTools[i] = toTool(executed.get(i));
    }
    assertArrayEquals("Wrong order of executed tools, was " + Arrays.toString(executedTools) + ", expected " + Arrays.toString(order), order, executedTools);
  }

  protected void assertRebuildDoesNothing(Tool tool) {
    doBuild(tool);
    assertOrder();
  }


  protected void doBuild(Tool tool) {
    Input i = new Input(dir);
    executed = new ArrayList<>();
    // this anonymous inner class is to extend the standard logging with info about which builders
    // were invoked in what order. This is how the latex example does it, but TODO think about better way.
    IReporting reporting = new LogReporting() {
      @Override
      public <O extends Output> 
      void startedBuilder(BuildRequest<?, O, ?, ?> req, Builder<?, ?> b, BuildUnit<O> oldUnit, Set<BuildReason> reasons) {
        executed.add(req.factory);
        super.startedBuilder(req, b, oldUnit, reasons);
      }
    };
 
    try {
      switch(tool) { // TODO think about ways to do this! obvious toBuilderFactory doesn't work because can't infer types...
      case MODEL: BuildManagers.build(new BuildRequest<>(ModelBuilder.factory, i), reporting); break;
      case CODE: BuildManagers.build(new BuildRequest<>(CodeBuilder.factory, i), reporting); break;
      case TEST: BuildManagers.build(new BuildRequest<>(TestBuilder.factory, i), reporting); break;
      }
    } catch (Throwable err) {
      System.out.println("doBuild caught Throwable: "+err.getMessage());
    }
  }

  // Since in the orientation model nothing impacts the model, only the model's builder is invoked; it
  // discovers that it does not have to invoke any others.
  @Test
  public void testModel() {
    doBuild(MODEL);
    assertOrder(MODEL);
  }
  
  // The incrementality ensures that if we rebuild a thing, and then immediately rebuild it, the algorithm
  // detects that nothing needs to be done.
  @Test
  public void testModelWithRebuild() {
    doBuild(MODEL);
    assertOrder(MODEL);
    assertRebuildDoesNothing(MODEL);
  }
  
 // Since in the orientation model both safeConforms (from test) and roundTripConforms (from model) impact
 // the code, the code's builder discovers that it has to invoke both test's and code's builders, to make
 // sure that they are up to date, before it updates code with respect to test and code. As it happens,
 // the code's builder breqs the model's builder first, then the test's builder.
  @Test
  public void testCode() {
    doBuild(CODE);
    assertOrder(CODE, MODEL, TEST);
    assertRebuildDoesNothing(CODE);
  }

 // Since in the orientation model nothing impacts the test, only the test's builder is invoked; it
 // discovers that it does not have to invoke any others. 
  @Test
  public void testTest() {
    doBuild(TEST);
    assertOrder(TEST);
    assertRebuildDoesNothing(TEST);
  }
}
