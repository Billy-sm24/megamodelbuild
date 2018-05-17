package uk.ac.ed.inf.megamodelbuild;

import uk.ac.ed.inf.megamodelbuild.MegaBuilder.Input;
import java.io.File;

// I think in the end the OrientationModel should be what knows about all the models, where they are,
// how they are connected, etc. Then it should be auto-generated from a megamodel in a standard
// megamodelling language. For now, I'm just trying to get some embarrassing boilerplate out of the 
// builders. TODO
public class OrientationModel {

  private final File orientationModel;

  // TODO should this be a singleton? For now, never mind, only immutable data!
  // TODO think about filenames in general. Where should they come from?
  public OrientationModel(Input input) {
    orientationModel = new File(input.dir, "orientation.txt");
  }

  public File getFile() {
    return orientationModel;
  }

  // post: return is not null
  public String getInfoFor(String prefix, OrientationStamper stamper) throws MegaException {
    String orientationInfo = stamper.stampOf(orientationModel).val;
    if (null == orientationInfo) {
      String error = "No line found for " + prefix + " in orientation model!";
      // TODO think about error handling - can't use builder's report here
      throw new MegaException(error);
    }
    return orientationInfo;
  }

  // TODO better API, see above :-)
  public static boolean authoritative(String line) {
    String[] orientationInfoPieces = line.split("\\s+");
    // first piece in the stamp is y or n for whether or not we're authoritative
    return (orientationInfoPieces[0].equals("y"));
  }

  public static String[] relationsToRestore(String line) {
    String[] orientationInfoPieces = line.split("\\s+");
    String[] relations = new String[orientationInfoPieces.length - 1];
    for (int i = 0; i < relations.length; i++) {
      relations[i] = orientationInfoPieces[i + 1]; // yes yes, this is dumb, I
                                                   // know
    }
    return relations;
  }

}
