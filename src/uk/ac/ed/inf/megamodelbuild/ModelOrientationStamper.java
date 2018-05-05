package uk.ac.ed.inf.megamodelbuild;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sugarj.common.FileCommands;
import org.sugarj.common.util.Pair;

import build.pluto.stamp.Stamper;
import build.pluto.stamp.ValueStamp;

// A kind of stamper to be used only on orientation models. Given a prefix that identifies
// a model, it uses the (assumed unique) line with that prefix as the stamp.
// Because of pluto's conventional use of Singleton, we end up with a lot of this code duplicated
// across orientation stampers - TODO think about the right way to do this.
public class ModelOrientationStamper implements Stamper {
  
  private static final String prefix = "Model:";

  public static ModelOrientationStamper instance = new ModelOrientationStamper();
  private ModelOrientationStamper() { }
  
  @Override
  public ValueStamp<String> stampOf(File p) {
    String content;
    try {
      content = FileCommands.readFileAsString(p);
    } catch (FileNotFoundException e) {
      return new ValueStamp<>(this, null);
    } catch (IOException e) {
      e.printStackTrace();
      return new ValueStamp<>(this, null);
    }

    for (String line : content.split("\n")) {
      if (line.startsWith(prefix)) {
        return new ValueStamp<>(this, line.substring(prefix.length()).trim());
      }
    }
    return new ValueStamp<>(this, null);
  }
}
