#!/bin/bash

for FILE in *.gz; do
	gunzip ${FILE}

done
