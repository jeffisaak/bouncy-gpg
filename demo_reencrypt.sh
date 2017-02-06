#!/usr/bin/env bash

#
# decrypts an encrypted ZIP file and reencrypts each file in the zip
#

ASSEMBLY=bouncy-castle-examples-1.1.0
LOCATION=./build

#DRIVER_CLASS=name.neuhalfen.projects.crypto.bouncycastle.openpgp.example.MainExplodedSinglethreaded
DRIVER_CLASS=name.neuhalfen.projects.crypto.bouncycastle.openpgp.example.MainExplodedMultithreaded

DEST=/tmp/gpg-example-$$

[ -d "${DEST}" ] && rm -rf "${DEST}" 
mkdir $DEST || exit 1
echo Writing results into \"$DEST\"

[ -f ./build/libs/${ASSEMBLY}.jar ] ||  ./gradlew installDist

CP=${LOCATION}/libs/${ASSEMBLY}.jar
for JAR in ${LOCATION}/install/bouncy-castle-examples/lib/*.jar
do
   CP=${CP}:${JAR}
done

time java -cp ${CP} \
   ${DRIVER_CLASS} \
   recipient@example.com \
   ./src/test/resources/recipient.gpg.d/pubring.gpg  \
   ./src/test/resources/recipient.gpg.d/secring.gpg recipient \
   "src/test/resources/testdata/large_demo__1GB_data.zip.gpg" "${DEST}"


echo
echo Results in $DEST