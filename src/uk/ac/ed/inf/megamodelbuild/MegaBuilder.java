package uk.ac.ed.inf.megamodelbuild;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import build.pluto.builder.Builder;
import build.pluto.output.Out;
import build.pluto.output.OutputPersisted;
import build.pluto.stamp.LastModifiedStamper;
import build.pluto.stamp.Stamper;
import uk.ac.ed.inf.megamodelbuild.orientationmodel.Model;
import uk.ac.ed.inf.megamodelbuild.orientationmodel.OrientationModel;

public abstract class MegaBuilder extends Builder<MegaBuilder.Input, Out<File>> {

  public MegaBuilder(Input input) {
    super(input);
  }
  
  protected abstract String getName();
  protected abstract String getFileName();
  protected abstract OrientationStamper getOrientationStamper();
  protected abstract void restoreConsistency(Input input, File code, Model orientationInfo) throws MegaException, IOException;
  
  public static class Input implements Serializable {
    public final File dir;

    // argument must not be null
    public Input(File dir) {
      this.dir = dir;
    }
  }

  @Override
  protected String description(Input input) {
    return "Build "+getName();
  }

  @Override
  public File persistentPath(Input input) {
    return new File(input.dir, getName()+".dep");
  }

  @Override
  protected Stamper defaultStamper() {
    return LastModifiedStamper.instance;
  }
  
  @Override
  protected Out<File> build(Input input) throws IOException, MegaException {
    File file = new File(input.dir, getFileName());
    OrientationModel orientationModel = new OrientationModel(input);
    File om = orientationModel.getFile();
    require(om, getOrientationStamper());
    boolean isAuthoritative = orientationModel.isAuthoritative(getName());
    if (isAuthoritative) {
      // do nothing
      report(getName()+" is authoritative, so no resolution to be done");
    } else {
      // do the actual work
      restoreConsistency(input, file, orientationModel.getModel(getName()));
    }
    provide(file);
    return OutputPersisted.of(file);
  }
}

