package uk.ac.ed.inf.megamodelbuild.orientationmodel;

import java.io.File;
import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import smith.billy.orientationmodel.OrientationModelStandaloneSetupGenerated;
import smith.billy.orientationmodel.orientationModel.Orientation_Model;

public class OrientationParser {
  
  private static final String FILE_EXTENSION = ".orientation";

  protected static HashMap<String, Model> parse(String path) {
    Injector injector = new OrientationModelStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
    Resource resource = resourceSet.getResource(
        URI.createFileURI(new File(path + "/orientation" + FILE_EXTENSION).getAbsolutePath()), true);
    
    return extractModels(resource);
  }
  
  private static HashMap<String, Model> extractModels(Resource resource) {
    HashMap<String, Model> models = new HashMap<>();
    
    Orientation_Model orientation = (Orientation_Model) resource.getContents().get(0);
    
    for (smith.billy.orientationmodel.orientationModel.Model rawModel : orientation.getModels()) {
      models.put(rawModel.getName(), new Model(rawModel));
    }
    
    return models;
  }

}
