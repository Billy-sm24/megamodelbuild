package uk.ac.ed.inf.megamodelbuild.test.build;

import static uk.ac.ed.inf.megamodelbuild.test.build.MegamodelBuildTest.Tool.MODEL;
import static uk.ac.ed.inf.megamodelbuild.test.build.MegamodelBuildTest.Tool.CODE;
import static uk.ac.ed.inf.megamodelbuild.test.build.MegamodelBuildTest.Tool.TEST;
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
import uk.ac.ed.inf.megamodelbuild.bxexample.CodeBuilder;
import uk.ac.ed.inf.megamodelbuild.bxexample.ModelBuilder;
import uk.ac.ed.inf.megamodelbuild.bxexample.TestBuilder;

import static uk.ac.ed.inf.megamodelbuild.bxexample.ModelBuilder.Input;

public class MegamodelBuildTest extends ScopedBuildTest {

//by some magic in ScopedBuildTest, this will end up pointing at the directory for each individual test
 @ScopedPath("")
 private File dir;
 
 // and these at the local copies of the files we manipulate
 @ScopedPath("Code.java")
 private File codeFile;

 @ScopedPath("Model.xmi")
 private File modelFile;
 
 @ScopedPath("Safety.txt")
 private File safetyFile;
 
 @ScopedPath("Test.java")
 private File testFile;
 
 @ScopedPath("orientation.txt")
 private File orientationFile;
 
//created anew by each doBuild, examined by assertOrder. Will work just fine 
// till we start running tests in parallel and then break horribly, I fear...
 private List<BuilderFactory<?, ?, ?>> executed;
 
  protected static enum Tool {
    MODEL,
    CODE,
    TEST
  }

  // Pre: there must always be a doBuild before an assertOrder, so executed will not be null.
  // Check that the order in which the builders were executed in response to that doBuild is as specified in order
  private void assertOrder(Tool... order) {
    Tool[] executedTools = new Tool[executed.size()];
    for (int i = 0; i < executedTools.length; i++)
      if (executed.get(i) == ModelBuilder.factory)
        executedTools[i] = MODEL;
      else if (executed.get(i) == CodeBuilder.factory)
        executedTools[i] = CODE;
      else if (executed.get(i) == TestBuilder.factory)
        executedTools[i] = TEST;
    
    assertArrayEquals("Wrong order of executed tools, was " + Arrays.toString(executedTools) + ", expected " + Arrays.toString(order), order, executedTools);
  }

  private void assertRebuildDoesNothing(Tool tool) {
    doBuild(tool);
    assertOrder();
  }


  private void doBuild(Tool tool) {
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
      switch(tool) { // TODO think about pros and cons of ways to do this!
      case MODEL: BuildManagers.build(new BuildRequest<>(ModelBuilder.factory, i), reporting); break;
      case CODE: BuildManagers.build(new BuildRequest<>(CodeBuilder.factory, i), reporting); break;
      case TEST: BuildManagers.build(new BuildRequest<>(TestBuilder.factory, i), reporting); break;
      }
    } catch (Throwable err) {
      System.out.println("doBuild caught Throwable: "+err.getMessage());
    }
  }

  @Test
  public void testModel() {
    doBuild(MODEL);
    assertOrder(MODEL);
  }
  
  @Test
  public void testModelWithRebuild() {
    doBuild(MODEL);
    assertOrder(MODEL);
    assertRebuildDoesNothing(MODEL);
  }
  
  @Test
  public void testCode() {
    doBuild(CODE);
    assertOrder(CODE, MODEL, TEST);
    assertRebuildDoesNothing(CODE);
  }

  @Test
  public void testTest() {
    doBuild(TEST);
    assertOrder(TEST);
    assertRebuildDoesNothing(TEST);
  }
}
