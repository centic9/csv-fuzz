#!/bin/sh
#
#
# Small helper script to produce a coverage report when executing the fuzz-model
# against the current corpus.
#
# You need to enable the test in class CorpusCoverageTest
#

set -eu


# Build the fuzzer and fetch dependency-jars
./gradlew shadowJar getDeps


# extract jar-files of Apache Commons CSV
mkdir -p build/csvFiles
cd build/csvFiles

# then unpack the class-files
for i in `find ../runtime -name commons-csv-*.jar`; do
  echo $i
  unzip -o -q $i
done

cd -


# Fetch JaCoCo Agent
test -f jacoco-0.8.13.zip || wget --continue https://repo1.maven.org/maven2/org/jacoco/jacoco/0.8.13/jacoco-0.8.13.zip
unzip -o jacoco-0.8.13.zip lib/jacocoagent.jar
mv lib/jacocoagent.jar build/
rmdir lib

mkdir -p build/jacoco


# Run Jazzer with JaCoCo-Agent to produce coverage information
./jazzer \
  --cp=build/libs/csv-fuzz-all.jar \
  --instrumentation_includes=org.apache.commons.** \
  --target_class=org.dstadler.csv.fuzz.Fuzz \
  --nohooks \
  --jvm_args="-javaagent\\:build/jacocoagent.jar=destfile=build/jacoco/corpus.exec" \
  -rss_limit_mb=4096 \
  -runs=0 \
  corpus
./jazzer \
  --cp=build/libs/csv-fuzz-all.jar \
  --instrumentation_includes=org.apache.commons.** \
  --target_class=org.dstadler.csv.fuzz.FuzzComplex \
  --nohooks \
  --jvm_args="-javaagent\\:build/jacocoagent.jar=destfile=build/jacoco/corpusComplex.exec" \
  -rss_limit_mb=4096 \
  -runs=0 \
  corpusComplex


# Finally create the JaCoCo report
java -jar /opt/poi/lib/util/jacococli.jar report build/jacoco/corpus.exec build/jacoco/corpusComplex.exec \
 --classfiles build/csvFiles \
 --sourcefiles /opt/apache/commons-csv/git/src/main/java/ \
 --html build/reports/jacoco


echo All Done, report is at build/reports/jacoco/index.html
