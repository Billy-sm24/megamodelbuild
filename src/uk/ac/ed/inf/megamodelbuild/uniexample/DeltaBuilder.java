package uk.ac.ed.inf.megamodelbuild.uniexample;

import java.io.File;
import java.io.IOException;

import org.sugarj.common.FileCommands;

import build.pluto.builder.factory.BuilderFactory;
import build.pluto.builder.factory.BuilderFactoryFactory;
import build.pluto.output.Out;
import uk.ac.ed.inf.megamodelbuild.MegaBuilder;
import uk.ac.ed.inf.megamodelbuild.MegaException;
import uk.ac.ed.inf.megamodelbuild.OrientationModel;
import uk.ac.ed.inf.megamodelbuild.OrientationStamper;

public class DeltaBuilder extends MegaBuilder {

  public static BuilderFactory<Input, Out<File>, DeltaBuilder> factory = BuilderFactoryFactory.of(DeltaBuilder.class, Input.class);

  public DeltaBuilder(Input input) {
    super(input);
  }

  public String getName() {
    return "delta";
  }

  protected String getFileName() {
    return "Delta.txt";
  }

  protected OrientationStamper getOrientationStamper() {
    return DeltaOrientationStamper.instance;
  }

  protected void restoreConsistency(Input input, File model, String orientationInfo) throws MegaException, IOException {
    boolean needToRestoreCompare = false;
    boolean needToRestorePatch = false;
    boolean needM1 = false;
    boolean needM2 = false;
    File m1 = new File(input.dir, "Model1.txt");
    File m2 = new File(input.dir, "Model2.txt");

    String[] relationsToRestore = OrientationModel.relationsToRestore(orientationInfo);
    for (String s : relationsToRestore) {
      if (s.equals("compare")) {
        needToRestoreCompare = true;
        needM1 = true;
        needM2 = true;
      }
      if (s.equals("patch")) {
        needToRestorePatch = true;
        needM1 = true;
        needM2 = true;
      }
    }
    if (needM1) { // always-authoritative, so no builder
      require(m1); 
    }
    if (needM2) {
      Out<File> m2FileWrapper = requireBuild(M2Builder.factory, input);
      m2 = m2FileWrapper.val();
      require(m2);
    }

    String newContent = "";
    if (needToRestoreCompare) {
      newContent += "\nNew content of delta is the compare of current m1, i.e.\n";
      newContent += FileCommands.readFileAsString(m1);
      newContent += "\n and current m2 i.e.\n";
      newContent += FileCommands.readFileAsString(m2);
    }
    if (needToRestorePatch) {
      // Panic, because the megamodel doesn't provide the means to do that. See comments in
      // M2Builder.
      throw new MegaException("Orientation model asks us to restore patch towards delta, but we have no means to do that");
    }
    report("Writing new " + getName() + " to file");
    FileCommands.writeToFile(model, newContent);
  }

}
