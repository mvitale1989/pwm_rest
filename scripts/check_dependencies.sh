#!/bin/bash

###CHECK IF APT IS INSTALLED
if ! (which dpkg > /dev/null 2>&1); then
	echo "ERROR: dpkg does not exist."
	echo "Non-debian system detected."
	echo "Dependency check not availabile. If build fails, refer to the project documentation."
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
	echo "ERROR: some dependencies are not satisfied. Compilation aborted."
	exit -1
else
	echo "SUCCESS: all dependencies are installed."
fi
