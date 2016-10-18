OS Version: macOS 10.12 (16A323)
=====================================================================
Extentional Libraries: slf4j-api-1.7.21.jar    slf4j-simple-1.7.21.jar
http://www.slf4j.org/download.html
======================================================================
To compile, put the above libraries in User/Library/Java/Extensions

To compile:

	javac *.java

To run:

	java KVStoreServer
	java KVStoreClient -server localhost:9090 -set key1 value1
	java KVStoreClient -server localhost:9090 -get key1
	java KVStoreClient -server localhost:9090 -del key1

