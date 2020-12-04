package uk.ac.ed.inf.megamodelbuild.test.build;

import static uk.ac.ed.inf.megamodelbuild.test.build.UniExampleTest.Tool.*;
import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.sugarj.common.FileCommands;

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
import uk.ac.ed.inf.megamodelbuild.uniexample.DeltaBuilder;
import uk.ac.ed.inf.megamodelbuild.uniexample.M2Builder;

public class UniExampleTest extends ScopedBuildTest {

//by some magic in ScopedBuildTest, this will end up pointing at the directory for each individual test
 @ScopedPath("")
 protected File dir;
 
//and these at the local copies of the files we manipulate - may need when we want to compare file contents
@ScopedPath("orientation.orientation")
private File orientationFile;

@ScopedPath("orientation2.orientation")
private File orientation2File;

 protected static enum Tool {
   M2,
   DELTA
 }
 
 // refactoring half way house
 protected Tool toTool(BuilderFactory<?, ?, ?> b) {
   if (b == M2Builder.factory) return M2;
   else if (b == DeltaBuilder.factory) return DELTA;
   else throw new Error("Unexpected factory "+b.toString());
 }
 
//created anew by each doBuild, examined by assertOrder. Will work just fine 
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
      case M2: BuildManagers.build(new BuildRequest<>(M2Builder.factory, i), reporting); break;
      case DELTA: BuildManagers.build(new BuildRequest<>(DeltaBuilder.factory, i), reporting); break;
      }
    } catch (Throwable err) {
      System.out.println("doBuild caught Throwable: "+err.getMessage());
    }
  }
  
  private static void replaceInFile(File file, String find, String replace) throws IOException {
    String content = FileCommands.readFileAsString(file);

    content = content.replaceAll(Pattern.quote(find), Matcher.quoteReplacement(replace));
    FileCommands.writeToFile(file, content);
}
  
  // replace orientation.txt with orientation2.txt
  private void switchOrientation() throws IOException {
    String content = FileCommands.readFileAsString(orientation2File);
    FileCommands.writeToFile(orientationFile, content);
    
  }

  // First some tests using a fixed orientation model like Fig 3a in the paper
  @Test
  public void testDelta() {
    doBuild(DELTA);
    assertOrder(DELTA, M2);
    assertRebuildDoesNothing(DELTA);
  }
  
  @Test
  public void testM2() {
    doBuild(M2);
    assertOrder(M2);
    assertRebuildDoesNothing(M2);
  }
  
  // and we should check automatically whether it actually does the right things with the files
  // Now some tests using a fixed orientation model like Fig 3b in the paper
  
  @Test
  public void testDeltaWithOtherOrientationModel() throws IOException {
    switchOrientation();
    doBuild(DELTA);
    assertOrder(DELTA);
    assertRebuildDoesNothing(DELTA);
  }
  
  @Test
  public void testM2WithOtherOrientationModel() throws IOException {
    switchOrientation();
    doBuild(M2);
    assertOrder(M2,DELTA);
    assertRebuildDoesNothing(M2);
  }
  
// Now let's demonstrate that the orietantation model is just a model, and try changing it.
// First, without any editing: that's not very interesting, because we leave the models consistent
// anyway before we change the orientation model. Still, let's check... 
  @Test
  public void testDeltaWithSwitchOfOrientationModel() throws IOException {
    doBuild(DELTA);
    assertOrder(DELTA, M2);
    assertRebuildDoesNothing(DELTA);
    switchOrientation();
    doBuild(DELTA);
    assertOrder(DELTA);
    assertRebuildDoesNothing(DELTA);
  }

  @Test
  public void testM2WithSwitchOfOrientationModel() throws IOException {
    doBuild(M2);
    assertOrder(M2);
    assertRebuildDoesNothing(M2);
    switchOrientation();
    doBuild(M2);
    assertOrder(M2,DELTA);
    assertRebuildDoesNothing(M2);
  }
}
