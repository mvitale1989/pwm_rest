#!/bin/bash

###CHECK IF APT IS INSTALLED
if ! (which dpkg > /dev/null 2>&1); then
	echo "ERROR: dpkg does not exist."
	echo "Non-debian system detected."
	echo "Please refer to the README.md file for compilation instructions."
	exit -1
fi

###CHECK DEPENDENCIES
DEPENDENCIES="libi2c-dev build-essential g++"
DEPENDENCY_NOT_SATISFIED=false
for DEPENDENCY in $DEPENDENCIES; do
	echo -n "Checking for installed dependency: ${DEPENDENCY}..."
	if ( dpkg --get-selections | grep -e "^${DEPENDENCY}" > /dev/null 2>&1 ); then
		echo "OK"
	else
		echo "NOT FOUND"
		DEPENDENCY_NOT_SATISFIED=true
	fi
done

if [ ${DEPENDENCY_NOT_SATISFIED} != false ]; then
	echo "Some dependencies are not satisfied. Compilation aborted."
	exit -1
else
	echo "Package check complete."
fi

###COMPILE EVERYTHING
echo "Compiling dynamic library..."
if (cd native && make library); then
	echo "Dynamic library compiled successfully."
else
	echo -e "ERROR.\nCheck compiler errors. Compilation aborted."
	exit -1
fi
