package uk.ac.ed.inf.megamodelbuild.bxexample;

import java.io.File;
import java.io.IOException;

import org.sugarj.common.FileCommands;

import build.pluto.builder.factory.BuilderFactory;
import build.pluto.builder.factory.BuilderFactoryFactory;
import build.pluto.output.Out;
import uk.ac.ed.inf.megamodelbuild.MegaBuilder;
import uk.ac.ed.inf.megamodelbuild.OrientationStamper;

public class TestBuilder extends MegaBuilder {

  public static BuilderFactory<Input, Out<File>, TestBuilder> factory = BuilderFactoryFactory.of(TestBuilder.class, Input.class);

  public TestBuilder(Input input) {
    super(input);
  }

  public String getName() {
    return "test";
  }

  protected String getFileName() {
    return "Test.java";
  }

  protected OrientationStamper getOrientationStamper() {
    return TestOrientationStamper.instance;
  }

  protected void restoreConsistency(Input input, File test, String orientationInfo) throws IOException {

    // other pieces in the stamp are identifiers for the megamodel edges that
    // have been oriented to point here
    // the megamodel should let us use those identifiers to find out which
    // models are sources for those edges
    // so suppose it did: for now we hardcode the result.
    File code = new File(input.dir, "Code.java");
    File safety = new File(input.dir, "Safety.txt");
    File sourceModels[] = { code, safety };
    for (File s : sourceModels) {
      require(s);
    }
    // Here's where we actually have to do some work.
    // In real life we probably call an external transformation engine to update
    // tests for consistency with code and safety.
    // Let's pretend:
    String newContent = "New tests that have magically restored consistency between:\nOld tests, i.e.";
    newContent += FileCommands.readFileAsString(test);
    newContent += "\n Current version of code, i.e.\n";
    newContent += FileCommands.readFileAsString(code);
    newContent += "\n Current version of safety, i.e.\n";
    newContent += FileCommands.readFileAsString(safety);
    report("Writing new " + getName() + " to file");
    FileCommands.writeToFile(test, newContent);
  }

}
