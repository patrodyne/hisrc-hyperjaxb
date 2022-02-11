#!/bin/sh
#
# Sort the XML schema inclusions in the dependency reverse order.
#
./schema-grep-includes.sh | tsort | tac
