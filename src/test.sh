#!/bin/bash
#Reggie Jones
#Group: Reggie Jones & Tristan Riddell
#Tests for project 3, CSE 332

touch output.txt

#assumes the CenPop file is 1 folder above since this is how eclipse manages this
for version in $@; do
	java PopulationQuery ../CenPop2010.txt 20 25 ${version} < input.txt > output.txt

	diff expected.txt output.txt
	echo "if no output above this line than ${version} passes this test"
done


