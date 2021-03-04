package uk.ac.ed.inf.megamodelbuild.orientationmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A sort of wrapper class for a model found in an orientation model.
 * XText produces its abstract syntax tree from a parsed file in the form
 * of ECore objects. Those objects are converted into this form so we can use
 * an overridden version of equals, along with some other important functionality.
 * It is also important that this class is serializable for use with value stamps -
 * this is also the reason we need the overridden equals method.
 * 
 * @author Billy
 */

public class Model implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private String name;
  private boolean authoritative;
  private List<Edge> targetEdges = new ArrayList<>();
  
  public Model(smith.billy.orientationmodel.orientationModel.Model rawModel,
      List<Edge> edges) {
    this.name = rawModel.getName();
    this.targetEdges = edges;
    
    if (rawModel.getAuthoritative().equals("True")) {
      this.authoritative = true;
    } else {
      this.authoritative = false;
    } 
  }

  public String getName() {
    return name;
  }
  
  public List<Edge> getEdges() {
    return targetEdges;
  }

  public boolean isAuthoritative() {
    return authoritative;
  }
  
  public boolean edgeNeedsRestoring(String edgeName) {
    for (Edge edge : targetEdges) {
      if (edge.getName().equals(edgeName)) {
        return true;
      } 
    }
    return false;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!getClass().equals(obj.getClass())) {
      return false;
    }
    
    // Compare state.
    Model other = (Model) obj;
    return Objects.equals(name, other.getName()) && 
        Objects.equals(authoritative, other.isAuthoritative()) &&
        targetEdges.equals(other.getEdges());
  }

}
