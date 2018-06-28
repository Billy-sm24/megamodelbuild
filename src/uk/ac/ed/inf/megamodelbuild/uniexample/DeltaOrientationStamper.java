package uk.ac.ed.inf.megamodelbuild.uniexample;

import java.io.ObjectStreamException;

import build.pluto.stamp.Stamper;
import uk.ac.ed.inf.megamodelbuild.OrientationStamper;

public class DeltaOrientationStamper extends OrientationStamper implements Stamper {

  private static final long serialVersionUID = 4815973204733186063L;
  public static DeltaOrientationStamper instance = new DeltaOrientationStamper();
  private DeltaOrientationStamper() { }
  private Object readResolve() throws ObjectStreamException { return instance; }
  
  protected String getPrefix() {return "delta";}
}
