package uk.ac.ed.inf.megamodelbuild.bxexample;

import java.io.File;
import java.io.IOException;

import org.sugarj.common.FileCommands;

import build.pluto.builder.factory.BuilderFactory;
import build.pluto.builder.factory.BuilderFactoryFactory;
import build.pluto.output.Out;
import uk.ac.ed.inf.megamodelbuild.MegaBuilder;
import uk.ac.ed.inf.megamodelbuild.MegaException;
import uk.ac.ed.inf.megamodelbuild.OrientationStamper;
import uk.ac.ed.inf.megamodelbuild.orientationmodel.Model;

public class CodeBuilder extends MegaBuilder {

  public static BuilderFactory<Input, Out<File>, CodeBuilder> factory = BuilderFactoryFactory.of(CodeBuilder.class, Input.class);

  public CodeBuilder(Input input) {
    super(input);
  }

  public String getName() {
    return "code";
  }

  protected String getFileName() {
    return "Code.java";
  }

  protected OrientationStamper getOrientationStamper() {
    return CodeOrientationStamper.instance;
  }

  @Override
  protected void restoreConsistency(Input input, File code, Model orientationInfo) throws MegaException, IOException {
    boolean needToRestoreRoundtrip = false;
    boolean needToRestoreSafeConforms = false;
    boolean needModel = false;
    boolean needSafety = false;
    boolean needTest = false;
    File model = new File(input.dir, "Model.xmi");
    File safety = new File(input.dir, "Safety.txt");
    File test = new File(input.dir, "Test.java");
    
    if (orientationInfo.edgeNeedsRestoring("roundtripConforms")) {
      needToRestoreRoundtrip = true;
      needModel = true;
    }
    if (orientationInfo.edgeNeedsRestoring("safeConforms")) {
      needToRestoreSafeConforms = true;
      needSafety = true;
      needTest = true;
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
    report("Writing new " + getName() + " to file");
    FileCommands.writeToFile(code, newContent);
  }

}
