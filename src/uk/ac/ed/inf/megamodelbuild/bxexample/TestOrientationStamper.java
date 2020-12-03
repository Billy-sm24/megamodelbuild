package uk.ac.ed.inf.megamodelbuild.bxexample;

import java.io.ObjectStreamException;

import build.pluto.stamp.Stamper;
import uk.ac.ed.inf.megamodelbuild.OrientationStamper;
public class TestOrientationStamper extends OrientationStamper implements Stamper {
 
  private static final long serialVersionUID = 693671193598068942L;
  public static TestOrientationStamper instance = new TestOrientationStamper();
  private TestOrientationStamper() { }
  private Object readResolve() throws ObjectStreamException { return instance; }
  
  @Override
  protected String getModelName() {
    return "test";
  }

  
}
