#!/bin/bash
#Reggie Jones
#Group: Reggie Jones & Tristan Riddell
#Tests for project 3, CSE 332
#!!Note: Assumes CenPop2010.txt is 1 directory up. see line ~27 in this file
#Creates random input queries, then runs the given versions with
#these input queries and compares the versions output to each other
#so there are no discrepencies between the version numbers
#run this with the command "./test2.sh -v1 -v2 -v3 -v4 -v5"

#simulates user input of columns and rows- +2 so no 0's appear and that the
#direction coordinates below cannot be greater than these numbers
let col="$RANDOM % 100 + 2"
let row="$RANDOM % 100 + 2"

#build input file simulating user queries- +1 everwhere so no 0's appear 
touch randomInput.txt 
for VARIABLE in 1 2 3 4 5; do
	let east="$RANDOM % $col + 1"
	let north="$RANDOM % $row + 1"
	let west="$RANDOM % $east + 1"
	let south="$RANDOM % $north + 1"
	echo "$west $south $east $north" >> randomInput.txt	
done
echo "exit" >> randomInput.txt

#assumes the CenPop file is 1 folder above since this is how eclipse manages this
for version in $@; do
	java PopulationQuery ../CenPop2010.txt ${col} ${row} ${version} < randomInput.txt > output${version}.txt

	diff output${version}.txt output$1.txt
done
echo "no output besides this line means ${@} all produce same output"


#remove the output files- comment this loop and cat output and input
#files to verify with your eyeballs there's stuff actually happening  
for version in $@; do
	rm output${version}.txt
done
#remove input file
rm randomInput.txt
