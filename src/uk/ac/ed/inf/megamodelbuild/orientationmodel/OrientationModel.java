 package uk.ac.ed.inf.megamodelbuild.orientationmodel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import uk.ac.ed.inf.megamodelbuild.MegaBuilder.Input;

// I think in the end the OrientationModel should be what knows about all the models, where they are,
// how they are connected, etc. Then it should be auto-generated from a megamodel in a standard
// megamodelling language. For now, I'm just trying to get some embarrassing boilerplate out of the 
// builders. TODO
public class OrientationModel {
  
  private static OrientationModel instance = null;
  public File orientationFile;
  
  private HashMap<String, Model> models;

  // TODO should this be a singleton? For now, never mind, only immutable data!
  // TODO think about filenames in general. Where should they come from?
  public OrientationModel(Input orientationPath) {
    models = OrientationParser.parse(orientationPath.dir.getPath());
    this.orientationFile = orientationPath.dir;
  }
  
  // Without being a singleton, the tests fail.
  public static OrientationModel getInstance(Input orientationPath) {
    instance = new OrientationModel(orientationPath);
//    if (instance == null) {
//      instance = new OrientationModel(orientationPath);
//    } 
    return instance;
  }
  
  public File getFile() {
    return orientationFile;
  }
  
  public Model getModel(String modelName) {
    return models.get(modelName);
  }
  
  public boolean isAuthoritative(String modelName) {
    return models.get(modelName).isAuthoritative();
  }
  
  public boolean needToRestoreEdge(String modelName, String edgeName) {
    List<Edge> edges = relationsToRestore(modelName);
    
    for (Edge edge : edges) {
      if(edge.getName().equals(edgeName)) {
        return true;
      }
    }
    return false;
  }
  
  public List<Edge> relationsToRestore(String modelName) {
    return models.get(modelName).getEdges();
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
    OrientationModel other = (OrientationModel) obj;
    return orientationFile.equals(other.orientationFile) &&
        models.equals(other.models);
  }
  
}
