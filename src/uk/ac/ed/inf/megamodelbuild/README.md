This directory contains (an early proof of concept version of) a megamodel build framework which is an adaptation of the pluto build system to building from megamodels as described in
Towards Sound, Optimal and Flexible Building from Megamodels
by Perdita Stevens in MODELS'18.  

For now it's very basic: see the TODOs in the source files for some examples of things that should be improved in future.

MegaBuilder is the baseclass for model builders. Each model builder still has to implement its own restoreConsistency method, but the base class handles not invoking this unless some work may be required.

OrientationStamper is the baseclass for producing a stamper for each model builder to use on the orientation model. (The specialisation that each model builder must do is entirely boilerplate, see examples.)

MegaException is really only a label, for now.

OrientationModel provides an API wrapping the orientation model, which is (for now) a basic text file.

