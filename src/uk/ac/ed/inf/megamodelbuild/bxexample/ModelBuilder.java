package uk.ac.ed.inf.megamodelbuild.bxexample;

import java.io.File;
import java.io.IOException;

import org.sugarj.common.FileCommands;

import build.pluto.builder.factory.BuilderFactory;
import build.pluto.builder.factory.BuilderFactoryFactory;
import build.pluto.output.Out;
import uk.ac.ed.inf.megamodelbuild.MegaBuilder;
import uk.ac.ed.inf.megamodelbuild.OrientationModel;
import uk.ac.ed.inf.megamodelbuild.OrientationStamper;

public class ModelBuilder extends MegaBuilder {

  public static BuilderFactory<Input, Out<File>, ModelBuilder> factory = BuilderFactoryFactory.of(ModelBuilder.class, Input.class);

  public ModelBuilder(Input input) {
    super(input);
  }

  public String getName() {
    return "model";
  }

  protected String getFileName() {
    return "Model.xmi";
  }

  protected OrientationStamper getOrientationStamper() {
    return ModelOrientationStamper.instance;
  }

  protected void restoreConsistency(Input input, File model, String orientationInfo) throws IOException {

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
    boolean needToRestoreMetamodelConformance = false;
    boolean needMetamodel = false;
    boolean needCode = false;
    File metamodel = new File(input.dir, "MM.xmi");
    File code = new File(input.dir, "Code.java");

    String[] relationsToRestore = OrientationModel.relationsToRestore(orientationInfo);
    for (String s : relationsToRestore) {
      if (s.equals("metamodelConforms")) {
        needToRestoreMetamodelConformance = true;
        needMetamodel = true;
      }
      if (s.equals("roundtripConforms")) {
        needToRestoreRoundtrip = true;
        needCode = true;
      }
    }
    if (needMetamodel) {
      require(metamodel); // no builder to require, because metamodel is
                          // always-authoritative
    }
    if (needCode) {
      Out<File> codeFileWrapper = requireBuild(CodeBuilder.factory, input);
      code = codeFileWrapper.val();
      require(code);
    }

    String newContent = "";
    // Suppose the builder developer determines that we should start by
    // restoring consistency along the
    // metamodel conformance edge, if that's required at all
    if (needToRestoreMetamodelConformance) {
      newContent += "\nModelBuilder's magical roundtrip restoration between old model, i.e.\n";
      newContent += FileCommands.readFileAsString(model);
      newContent += "\n and current version of metamodel, i.e.\n";
      newContent += FileCommands.readFileAsString(metamodel);
    }
    // update for roundtripConforms, if required; if both were required, do some
    // magic fiddling
    if (needToRestoreRoundtrip) {
      newContent += "\nModelBuilder's magical roundtripConforms restoration using old model i.e.\n";
      newContent += FileCommands.readFileAsString(model);
      newContent += "\n and current code, i.e.\n";
      newContent += FileCommands.readFileAsString(code);
      if (needToRestoreMetamodelConformance) {
        newContent += "\nWe had to restore consistency along both edges: consider it done.\n";
      }
    }
    report("Writing new " + getName() + " to file");
    FileCommands.writeToFile(model, newContent);
  }

}
