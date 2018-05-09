package uk.ac.ed.inf.megamodelbuild.bxexample;

import build.pluto.stamp.Stamper;
import uk.ac.ed.inf.megamodelbuild.OrientationStamper;

public class ModelOrientationStamper extends OrientationStamper implements Stamper {

  private static final long serialVersionUID = 4815973204733186063L;
  public static ModelOrientationStamper instance = new ModelOrientationStamper();
  private ModelOrientationStamper() { }
  
  protected String getPrefix() {return "Model:";}
}
