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
import uk.ac.ed.inf.megamodelbuild.MegaBuilder.Input;
import uk.ac.ed.inf.megamodelbuild.orientationmodel.Model;
import uk.ac.ed.inf.megamodelbuild.orientationmodel.OrientationModel;

import java.io.File;

public abstract class OrientationStamper implements Stamper {

  private static final long serialVersionUID = 8979181448258315667L;

  protected abstract String getModelName();

  /*
   * (non-Javadoc)
   * 
   * @see build.pluto.stamp.Stamper#stampOf(java.io.File)
   */
  @Override
  public ValueStamp<Model> stampOf(File orientationFile) {
    OrientationModel orientation = OrientationModel.getInstance(new Input(orientationFile));
    Model model = orientation.getModel(getModelName());
    
    return new ValueStamp<>(this, model);
  }

}
