#!/bin/sh
#
# Extract XML schema inclusions to evaluate the dependency order.
#
TGTPATH="../target/doc/_2008A_XML"
cd "${TGTPATH}"
grep "include.*schemaLocation" *.xsd | \
	sed 's/:.*="/" "/' | \
	sed 's#/.*$##' | \
	sed 's/^/"/'
