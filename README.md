
# Megamodelbuild

Project home page: http://homepages.inf.ed.ac.uk/perdita/MegamodelBuild 

This project contains an early-stage demonstration of building on top of the pluto build system to enable sound, flexible and optimal building from megamodels. It demonstrates the examples from the paper 

Towards Sound, Optimal and Flexible Building from Megamodels by me, Perdita Stevens, in MODELS'18,

of which you can find a preprint via the project home page (http://homepages.inf.ed.ac.uk/perdita/MegamodelBuild). The July 2018 version of the project was accorded an Artefact Evaluated stamp and can be found as a zip at ReMoDD as http://remodd.org/node/582 ; the version currently at GitHub as https://github.com/PerditaStevens/megamodelbuild may be more recent.

## How to make use of this project

Right now there is little point in looking at this project without reading the paper first: the project hasn't yet reached the stage where it's sensible to write user documentation that's independent of the paper. So this section will freely use terminology that is explained in the paper.

### The source code

The main megamodelbuild package contains the beginnings of a framework for building from megamodels. It provides, for example, an abstract base class MegaBuilder to be inherited by each of the builders of models in a megamodelbuild system, and a class OrientationModel to serve as the megamodelbuild system's interface to an orientation model. (The orientation model itself, of course, is just a model - for now, in a simple textual syntax - and can be edited directly.) See the README in the package for more detail.

The packages bxexample and uniexample implement the examples described in the MODELS paper, by providing appropriate builders and stampers. See the README in each package.

### The tests

Two JUnit test classes in test, BxExampleTest and UniExampleTest, exercise the two examples, using data from testdata. Reading these test files is the best way to understand what is going on. A typical test invokes a builder of one of the models in the megamodel being built, and checks that the expected builders were invoked in the expected order (which depends on the current state of the orientation model and the related models).

The travis build passing stamp demonstrates that the tests pass; the test files can be run directly using maven (I'm using eclipse and typically right click pom.xml and select Run As -> Maven test) or individually by right clicking the test file and choosing Run as -> JUnit test.

### Dependencies

This project makes essential use of the pluto build system: there is a lot more information on that at http://pluto-build.github.io/ , though my MODELS paper (and the supplement at the project page http://homepages.inf.ed.ac.uk/perdita/MegamodelBuild/ ) give a summary that may be enough to be going on with. The Maven script of this project should handle that dependency for you; you should not need to install pluto manually.

Everything else is pretty standard... Java 8, JUnit...

## Future plans and how to contribute

I hope to take this project forwards towards being practically useful. This will obviously involve demonstrating it on less toy examples, which will require extending the framework by, for example, providing wrappers for transformation engines that can be used from the builders. If you'd like to get involved - for example, by contributing a wrapper for your favourite transformation engine, or suggesting an example to try it on - please do contact me.

## Licence

This project is licensed under the Apache 2.0 licence. (Not a very considered choice so far.)

## Acknowledgements

All the people thanked in the paper, plus the MODELS'18 Artefact Evaluation team (especially for being patient with me as a first-time submitter to an Artefact Evaluation process, and for suggesting additions to the documentation).

[![Build Status](https://travis-ci.com/PerditaStevens/megamodelbuild.svg?branch=master)](https://travis-ci.com/PerditaStevens/megamodelbuild)
