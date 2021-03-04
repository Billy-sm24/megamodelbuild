package uk.ac.ed.inf.megamodelbuild.orientationmodel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import smith.billy.orientationmodel.OrientationModelStandaloneSetupGenerated;
import smith.billy.orientationmodel.orientationModel.Orientation_Model;

public class OrientationParser {
  
  //private static final String FILE_EXTENSION = ".orientation";
  // Relative storage location for orientation models.
  //private static final String STORAGE_PATH = "orientationmodels/";
  
  protected static HashMap<String, Model> parse(String path) {
    Injector injector = new OrientationModelStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource resource = resourceSet.getResource(
        URI.createFileURI(new File(path + "/orientation.orientation").getAbsolutePath()), true);
    
    return extractModels(resource);
  }
  
  private static HashMap<String, Model> extractModels(Resource resource) {
    Orientation_Model orientation = (Orientation_Model) resource.getContents().get(0);
    HashMap<String, Model> processedModels = new HashMap<>();
    
    for (smith.billy.orientationmodel.orientationModel.Model rawModel : orientation.getModels()) {
      ArrayList<Edge> matchedEdges = new ArrayList<>();
      
      for (smith.billy.orientationmodel.orientationModel.Edge rawEdge : orientation.getEdges()) { 
        if (rawEdge.getTarget().getName().toString().equals(rawModel.getName().toString()) ) {
          matchedEdges.add(new Edge(rawEdge));
        }
      }
      processedModels.put(rawModel.getName().toString(), new Model(rawModel, matchedEdges));
    }
    return processedModels;
  }
}
