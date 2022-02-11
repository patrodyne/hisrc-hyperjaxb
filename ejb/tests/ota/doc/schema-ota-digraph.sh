#!/bin/sh
#
# Extract XML schema inclusions to evaluate the dependency order.
#
echo "digraph OTA {"
echo "\tranksep = 2.0;"
./schema-grep-includes.sh | \
	sed 's/^/\t/' | \
	sed 's/" "/" -> "/'
echo "}"
