#!/bin/bash

for FILE in *.json; do
	python -m json.tool ${FILE} > ${FILE}.filtered ; sleep 0.5 ; mv -f ${FILE}.filtered ${FILE}

done
