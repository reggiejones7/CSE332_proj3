#!/bin/bash
#Reggie Jones
#Group: Reggie Jones & Tristan Riddell
#Tests for project 3, CSE 332
#Tests the output of the several query inputs that Hye In gave as examples.
#The simulated user input queries are in input.txt file
#run this with commmand "./test.sh -v1 -v2 -v3 -v4 -v5"

touch output.txt

#assumes the CenPop file is 1 folder above since this is how eclipse manages this
for version in $@; do
	java PopulationQuery ../CenPop2010.txt 20 25 ${version} < input.txt > output.txt

	diff expected.txt output.txt
	echo "if no output above this line than ${version} passes this test"
done

#comment out line below and cat output.txt to verify there is an actual output and that I'm not just removing the output.txt trying to fool someone :)
rm output.txt


