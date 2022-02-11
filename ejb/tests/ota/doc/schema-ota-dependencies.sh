#!/bin/sh
#
# Use the dot command to generate a graph of the OTA schema dependencies.
# Use a web browser to view the SVG file.
#
./schema-ota-digraph.sh | \
	dot -Tsvg -Gfontcolor=red -Glabel="OTA" >schema-ota-dependencies.svg
