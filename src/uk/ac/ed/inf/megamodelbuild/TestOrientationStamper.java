package uk.ac.ed.inf.megamodelbuild;

import build.pluto.stamp.Stamper;
public class TestOrientationStamper extends OrientationStamper implements Stamper {
 
  private static final long serialVersionUID = 693671193598068942L;
  public static TestOrientationStamper instance = new TestOrientationStamper();
  private TestOrientationStamper() { }
  
  protected String getPrefix() {return "Test:";}
  
}
