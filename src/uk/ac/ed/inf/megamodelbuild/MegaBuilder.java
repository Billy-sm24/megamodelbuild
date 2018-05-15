package uk.ac.ed.inf.megamodelbuild;

import java.io.File;
import java.io.Serializable;

import build.pluto.builder.Builder;
import build.pluto.output.Out;
import build.pluto.stamp.LastModifiedStamper;
import build.pluto.stamp.Stamper;

public abstract class MegaBuilder extends Builder<MegaBuilder.Input, Out<File>> {

  public MegaBuilder(Input input) {
    super(input);
  }
  
  protected abstract String getName();
  
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

}

