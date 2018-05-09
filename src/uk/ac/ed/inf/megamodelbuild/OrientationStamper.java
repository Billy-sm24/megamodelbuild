/** This is just an abstract class to be used as the parent of each builder's stamper for the
 * orientation model, to avoid earlier copy-pasting. Obviously it could be much more flexible,
 * but this is all I need for now. Given a prefix that identifies a model, it uses the 
 * (assumed unique) line with that prefix as the stamp.
 **/
/**
 * @author perdita
 *
 */
package uk.ac.ed.inf.megamodelbuild;

import build.pluto.stamp.Stamper;
import build.pluto.stamp.ValueStamp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.sugarj.common.FileCommands;

public abstract class OrientationStamper implements Stamper {

  private static final long serialVersionUID = 8979181448258315667L;

  protected abstract String getPrefix();

  /*
   * (non-Javadoc)
   * 
   * @see build.pluto.stamp.Stamper#stampOf(java.io.File)
   */
  @Override
  public ValueStamp<String> stampOf(File p) {
    String prefix = getPrefix();
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
