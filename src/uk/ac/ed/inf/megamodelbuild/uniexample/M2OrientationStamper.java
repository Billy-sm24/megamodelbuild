package uk.ac.ed.inf.megamodelbuild.uniexample;

import java.io.ObjectStreamException;

import build.pluto.stamp.Stamper;
import uk.ac.ed.inf.megamodelbuild.OrientationStamper;

public class M2OrientationStamper extends OrientationStamper implements Stamper {
  
  private static final long serialVersionUID = 544159655901096L;
  public static M2OrientationStamper instance = new M2OrientationStamper();
  private M2OrientationStamper() { }
  private Object readResolve() throws ObjectStreamException { return instance; }
  
  @Override
  protected String getModelName() {return "m2";}

}
