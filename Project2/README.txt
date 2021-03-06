Distributed System Project2 - README

---Environment---
MacOS X v10.11
Java v1.7
Apache Thrift v0.9.3

---Files contains in the zipfile---
0. README
1. The java files. Need to be compiled.
2. sequenceassign.thrift, the thrift file of sequence server.
3. Folder kvstore and sequenceassign. Contains autogenerated thrift files.
4. Folder org. Contains thrift classes for servers and clients.
5. Two jar file slf4j-api-1.7.21.jar and slf4j-simple-1.7.21.jar. Additional necessary classes for servers and clients, need to be included in java classpath before compile the java files.
6. Three screenshot of testing on our local kvstore. We didn't submit the implementation of our local kvstore.

---How to compile---
1. Unzip the zipfile
2. Include "slf4j-api-1.7.21.jar" and "slf4j-simple-1.7.21.jar" to java classpath by copying them to "/Library/Java/Extensions/". (This directory may be different on other OS. Check the library extension directory of java on your system first.)
3. Use a terminal go to the unzipped directory. Type in "javac *.java" to compile all java files.

---How to run---
1. Run the sequence server. On terminal type in "java SequenceAssignServer". When you see "Starting the sequenceassign server...", the server has successfully started. One important thing is the sequence server running on localhost:9099, make sure the 9099 port is free before run the server.
2. Run Test. On terminal type in "java Test -server yourhost:port". (Replace yourhost:port to the host and port of your kvstore server.)

---How it works---
We implement a sequence server to provide a logical clock for evern read/write operation. The suquence number provided by the server is unique, non－negative and increasing. Each time before the operation, the client will ask for a sequence number from the sequence server, which is the start number. After the operation finishes, the client will ask for another sequence number, which is the end number.

We are to flood 10001 times of kvstore operations on a single key "testkey".
The first time is initialize the key's value to "0". The fllowing 10000 operations are 5000 reads and 5000 writes, running on 10 threads. 
This process should be down in several seconds, but weak network may influence the process time. So if the process time is too long, one could go to Test.java and reduce the value of REPEAT, which is on the 23 line. The total operation time is REPEAT * 10 + 1.

We generate a node for each operation and store them into a vector. After all the operations finished, we build a graph(trace) according to the algorithm presented by HP labs to analyze the consistency.

---Exit code---
System exit code is 0: The kvstore is consistent.
System exit code is 1: The kvstore is unconsistent.
System exit code is 2: The consistency of the kvstore is inconclusive. For example, the server stopped responding, wrong input parameters etc.


