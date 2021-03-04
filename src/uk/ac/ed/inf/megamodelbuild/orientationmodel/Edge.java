package uk.ac.ed.inf.megamodelbuild.orientationmodel;

import java.io.Serializable;
import java.util.Objects;

public class Edge implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private String name;
  private String source;
  private String target;

  public Edge(smith.billy.orientationmodel.orientationModel.Edge rawEdge) {
    this.name = rawEdge.getName();
    this.source = rawEdge.getSource().getName();
    this.target = rawEdge.getTarget().getName();
  }

  public String getName() {
    return name;
  }

  public String getSource() {
    return source;
  }
  
  public String getTarget() {
    return target;
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
    
    Edge other = (Edge) obj;
    return Objects.equals(name, other.getName()) &&
        Objects.equals(source, other.getSource()) &&
        Objects.equals(target,  other.getTarget());
  }
}
