package uk.ac.ed.inf.megamodelbuild.bxexample;

import java.io.ObjectStreamException;

import build.pluto.stamp.Stamper;
import uk.ac.ed.inf.megamodelbuild.OrientationStamper;

public class CodeOrientationStamper extends OrientationStamper implements Stamper {
  
  private static final long serialVersionUID = 544159655901096L;
  public static CodeOrientationStamper instance = new CodeOrientationStamper();
  private CodeOrientationStamper() { }
  private Object readResolve() throws ObjectStreamException { return instance; }
  
  protected String getModelName() {return "code";}
}
