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

public class M2Builder extends MegaBuilder {

  public static BuilderFactory<Input, Out<File>, M2Builder> factory = BuilderFactoryFactory.of(M2Builder.class, Input.class);

  public M2Builder(Input input) {
    super(input);
  }

  public String getName() {
    return "m2";
  }

  protected String getFileName() {
    return "Model2.txt";
  }

  protected OrientationStamper getOrientationStamper() {
    return M2OrientationStamper.instance;
  }

  protected void restoreConsistency(Input input, File code, String orientationInfo) throws IOException, MegaException {
    // This particular case is so simple there's only one thing we could have to do, but let's be
    // formulaic, towards more generation.
    boolean needToRestoreCompare = false;
    boolean needToRestorePatch = false;
    boolean needM1 = false;
    boolean needDelta = false;
    File m1 = new File(input.dir, "Model1.txt");
    File delta = new File(input.dir, "Delta.txt");

    String[] relationsToRestore = OrientationModel.relationsToRestore(orientationInfo);
    for (String s : relationsToRestore) {
      if (s.equals("compare")) {
        needToRestoreCompare = true;
        needM1 = true;
        needDelta = true;
      }
      if (s.equals("patch")) {
        needToRestorePatch = true;
        needM1 = true;
        needDelta = true;
      }
    }
    if (needM1) { // always-authoritative, so no builder
      require(m1);
    }
    if (needDelta) {
      Out<File> deltaFileWrapper = requireBuild(DeltaBuilder.factory, input);
      delta = deltaFileWrapper.val();
      require(delta);
    }
    String newContent = "";
    if (needToRestoreCompare) {
      // Panic, because we don't have any operations that can do that: the only valid way to orient the
      // compare edge is towards the Delta. 
      // Probably our megamodel notation ought to have arrows on the relations, in such a case, so that
      // if a relation is already oriented in the megamodel, that's the only valid way to orient it in the
      // orientation model? Or is it better to keep the megamodel notation more flexible?
      throw new MegaException("Orientation model asks us to restore compare towards m2, but we have no means to do that");
    }
    if (needToRestorePatch) {
      newContent += "Magical patching: what goes here is current m1 i.e.\n";
      newContent += FileCommands.readFileAsString(m1);
      newContent += "\n patched with current delta i.e.\n";
      newContent += FileCommands.readFileAsString(delta);
    }
    report("Writing new " + getName() + " to file");
    FileCommands.writeToFile(code, newContent);
  }

}
