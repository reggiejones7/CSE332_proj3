#!/bin/bash
#Reggie Jones
#Project 3
#tests that giving incorrect query coordinates terminates the program and prints error message

echo "101 10 10 10" > input.txt
echo "exit" >> input.txt
java PopulationQuery ../CenPop2010.txt 10 10 -v1 < input.txt 
rm input.txt
