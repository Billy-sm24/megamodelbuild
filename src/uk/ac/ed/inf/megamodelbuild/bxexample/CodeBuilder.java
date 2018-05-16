package uk.ac.ed.inf.megamodelbuild.bxexample;

import java.io.File;
import java.io.IOException;

import org.sugarj.common.FileCommands;

import build.pluto.builder.Builder;
import build.pluto.builder.factory.BuilderFactory;
import build.pluto.builder.factory.BuilderFactoryFactory;
import build.pluto.output.Out;
import build.pluto.output.OutputPersisted;
import build.pluto.stamp.LastModifiedStamper;
import build.pluto.stamp.Stamper;
import uk.ac.ed.inf.megamodelbuild.MegaBuilder;
import uk.ac.ed.inf.megamodelbuild.MegaException;

public class CodeBuilder extends MegaBuilder {

  public static BuilderFactory<Input, Out<File>, CodeBuilder> factory = BuilderFactoryFactory.of(CodeBuilder.class, Input.class);

  public String getName() { return "code"; }
  
  public CodeBuilder(Input input) {
    super(input);
  }

  @Override
  protected Out<File> build(Input input) throws IOException, MegaException {
    // boilerplate, can easily be generated from the megamodel:
    File code = new File(input.dir, "Code.java");
    File orientationModel = new File(input.dir, "orientation.txt");
    require(orientationModel, CodeOrientationStamper.instance);
    String orientationInfo = CodeOrientationStamper.instance.stampOf(orientationModel).val;
    if (null == orientationInfo) {
      String error = "No line found for "+getName()+" in orientation model!";
      reportError(error);
      throw new MegaException(error);
    }
    String[] orientationInfoPieces = orientationInfo.split("\\s+");
    // first piece in the stamp is y or n for whether or not we're authoritative
    boolean isAuthoritative = (orientationInfoPieces[0].equals("y"));
    if (isAuthoritative) {
      // do nothing
      report(getName()+" is authoritative, so no resolution to be done");
    } else {
      // Not quite boilerplate: depends what actually needs to be done.
      // Formulaic though. There's an
      // interesting design question about how to arrange this. Later.
      // other pieces in the stamp are identifiers for the megamodel edges that
      // have been oriented to point here
      // the megamodel should let us use those identifiers to find out which
      // models are sources for those edges
      // so suppose it did: for now we hardcode the result.
      // Look at the edges we have to restore consistency along;
      // look at the sources of those edges
      // do the right thing to restore consistency (which may vary of course
      // depending on what the edges are!)
      boolean needToRestoreRoundtrip = false;
      boolean needToRestoreSafeConforms = false;
      boolean needModel = false;
      boolean needSafety = false;
      boolean needTest = false;
      File model = new File(input.dir, "Model.xmi");
      File safety = new File(input.dir, "Safety.txt");
      File test = new File(input.dir, "Test.java");

      for (String s : orientationInfoPieces) {
        if (s.equals("roundtripConforms")) {
          needToRestoreRoundtrip = true;
          needModel = true;
        }
        if (s.equals("safeConforms")) {
          needToRestoreSafeConforms = true;
          needSafety = true;
          needTest = true;
        }
      }
      if (needModel) {
        Out<File> modelFileWrapper = requireBuild(ModelBuilder.factory, input);
        model = modelFileWrapper.val();
        require(model);
      }
      if (needSafety) {
        require(safety); // always-authoritative, so no builder
      }
      if (needTest) {
        Out<File> testFileWrapper = requireBuild(TestBuilder.factory, input);
        test = testFileWrapper.val();
        require(test);
      }
      String newContent = "";
      // Suppose the builder developer determines that we should start by
      // restoring consistency along the
      // roundtrip edge, if that's required at all
      if (needToRestoreRoundtrip) {
        // Should be something like:
        // QVTdEngine.invoke(model, code, true);
        // but for now let's pretend:
        newContent += "Magical roundtrip restoration between old code, i.e.\n";
        newContent += FileCommands.readFileAsString(code);
        newContent += "\n and current version of model, i.e.\n";
        newContent += FileCommands.readFileAsString(model);
      }
      // update for safeConforms, if required; if both were required, do some
      // magic fiddling
      if (needToRestoreSafeConforms) {
        newContent += "Magical safeConforms restoration using current safety model i.e.\n";
        newContent += FileCommands.readFileAsString(safety);
        newContent += "\n and current tests, i.e.\n";
        newContent += FileCommands.readFileAsString(test);
        if (needToRestoreRoundtrip) {
          newContent += "\nWe had to restore consistency along both edges: consider it done.\n";
        }
      }
      report ("Writing new "+getName()+" to file");
      FileCommands.writeToFile(code, newContent);
    }
    // more boilerplate:
    provide(code);
    return OutputPersisted.of(code);
  }

}
