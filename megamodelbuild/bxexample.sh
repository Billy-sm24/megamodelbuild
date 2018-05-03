#/bin/sh

ARGS="bxexample uk.ac.ed.inf.megamodelbuild.CodeBuilder.factory uk.ac.ed.inf.megamodelbuild.CodeBuilder\$Input $@"

mvn compile exec:java -Dexec.args="$ARGS"
