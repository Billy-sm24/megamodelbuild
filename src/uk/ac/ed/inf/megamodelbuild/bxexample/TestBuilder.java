package uk.ac.ed.inf.megamodelbuild.bxexample;

import java.io.File;
import java.io.IOException;

import org.sugarj.common.FileCommands;

import build.pluto.builder.factory.BuilderFactory;
import build.pluto.builder.factory.BuilderFactoryFactory;
import build.pluto.output.Out;
import build.pluto.output.OutputPersisted;
import uk.ac.ed.inf.megamodelbuild.MegaBuilder;
import uk.ac.ed.inf.megamodelbuild.MegaException;

public class TestBuilder extends MegaBuilder {

  public static BuilderFactory<Input, Out<File>, TestBuilder> factory = BuilderFactoryFactory.of(TestBuilder.class, Input.class);
  
  public String getName() { return "test"; }

  public TestBuilder(Input input) {
    super(input);
  }

  @Override
  protected Out<File> build(Input input) throws IOException, MegaException {
    // all this is boilerplate, can easily be generated:
    File test = new File(input.dir, "Test.java");
    File orientationModel = new File(input.dir, "orientation.txt");
    require(orientationModel, TestOrientationStamper.instance);
    String orientationInfo = TestOrientationStamper.instance.stampOf(orientationModel).val;
    if (null == orientationInfo) {
      String error = "No line found for model in orientation model!";
      reportError(error);
      throw new MegaException(error);
    }
    String[] orientationInfoPieces = orientationInfo.split("\\s+");
    // first piece in the stamp is y or n for whether or not we're authoritative
    boolean isAuthoritative = (orientationInfoPieces[0].equals("y")); 
    if (isAuthoritative) {
      // do nothing
    } else {
      // other pieces in the stamp are identifiers for the megamodel edges that have been oriented to point here
      // the megamodel should let us use those identifiers to find out which models are sources for those edges
      // so suppose it did: for now we hardcode the result.
      File code = new File(input.dir, "Code.java");
      File safety = new File(input.dir, "Safety.txt");
      File sourceModels[] = {code, safety};
      for (File s : sourceModels) {
        require(s);
      }
      // Here's where we actually have to do some work.
      // In real life we probably call an external transformation engine to update tests for consistency with code and safety.
      // Let's pretend:
      String newContent = "New tests that have magically restored consistency between:\nOld tests, i.e.";
      newContent +=  FileCommands.readFileAsString(test);
      newContent += "\n Current version of code, i.e.\n";
      newContent += FileCommands.readFileAsString(code);
      newContent += "\n Current version of safety, i.e.\n";
      newContent += FileCommands.readFileAsString(safety);
      FileCommands.writeToFile(test, newContent);
    }
    // more boilerplate:
    provide(test);
    return OutputPersisted.of(test);
  }

}
