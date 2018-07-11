This directory contains files implementing the unidirectional examples from the paper 
Towards Sound, Optimal and Flexible Building from Megamodels
by Perdita Stevens in MODELS'18.  

The megamodel contains two models that are not always-authoritative, viz:
Delta
M2
plus M1 which is always-authoritative, i.e., which we
never want to be automatically updated by a build process (it is edited only manually).

For each of Delta and M2, therefore, we implement a builder, making use of the
framework capabilities. A builder's interesting method is the one called restoreConsistency.
This must follow the pattern described in the paper, consulting the orientation model to see
what other builders, if any, must be breqed (using requireBuild) and what files must be freqed
(using require) and then doing whatever is necessary to construct a new version of the model
it is responsible for, that will be consistent with the models at the other ends of incoming
edges. If it can't do this it must fail.

Each of Delta and M2 also has an appropriate Stamper for use on the orientation model.
This captures the fact that changes to the orientation model that do not affect the relevant
line can be ignored.

See the relevant test and testdata for how these work together.

To note: this is an early proof of concept and we should be able to make it much slicker -
especially, require less work from developers of builders - in future.
