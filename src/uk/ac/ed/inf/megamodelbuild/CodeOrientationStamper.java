package uk.ac.ed.inf.megamodelbuild;

import build.pluto.stamp.Stamper;

public class CodeOrientationStamper extends OrientationStamper implements Stamper {
  
  private static final long serialVersionUID = 544159655901096L;
  
  public static CodeOrientationStamper instance = new CodeOrientationStamper();
  private CodeOrientationStamper() { }
  
  protected String getPrefix() {return "Code:";}
}
