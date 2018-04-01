## Simple Apache Pulsar Producer & Consumer Example

#### Prereq's

You'll need docker installed.  You can stand up Apache Pulsar on your local
by executing the following docker command.  Note this command will mount a volume call `data` in the directory 
where you run the command.  I have set up the `.gitignore` to ignore this folder for you. 

```bash
$ docker run -it \
  -p 6650:6650 \
  -p 8080:8080 \
  -v $PWD/data:/pulsar/data \
  apachepulsar/pulsar:1.22.0-incubating \
  bin/pulsar standalone --advertised-address 127.0.0.1
```

`8080` is mapped to the admin interface.  You can see metrics by hitting
`http://localhost:8080/admin/persistent/sample/standalone/ns1/my-topic/stats`
in your browser.

`6650` is the port you connect to with a Pulsar client.  

To build the project execute the following command from the root folder of the project

```bash
$ mvn package
```

To execute the application run, also in the root folder.
```bash
$ java -Dexec.mainClass="org.apache.pulsar.example.PulsarExample"
```

You should see output similar to the below

```bash

######## Producing Messages Below ########


Not lucky message: 2:90:-1
Not lucky message: 2:91:-1
Not lucky message: 2:92:-1
Not lucky message: 2:93:-1
Not lucky message: 2:94:-1
Lucky number 5 Message Id is: 2:95:-1
Not lucky message: 2:96:-1
Not lucky message: 2:97:-1
Not lucky message: 2:98:-1
Not lucky message: 2:99:-1


######## Consuming Messages Below ########


Not lucky response: 2:90:-1
Not lucky response: 2:91:-1
Not lucky response: 2:92:-1
Not lucky response: 2:93:-1
Not lucky response: 2:94:-1
lucky number 5 id 2:95:-1, message lucky-five: 
Not lucky response: 2:96:-1
Not lucky response: 2:97:-1
Not lucky response: 2:98:-1
Not lucky response: 2:99:-1
```

You can see in the main class that I am grabbing the `luckNumberMessageId`  
after pushing it to the Pulsar topic.
```java
if (i == luckyNumber) {
    bytes = "lucky-five".getBytes();
    luckNumberMessageId = producer.send(bytes);
    System.out.println("Lucky number 5 Message Id is: " + luckNumberMessageId);

}
``` 

I am then printing a special message once I pull the correct message off of the pulsar topic.

```java
if (luckNumberMessageId !=  null && msg.getMessageId().equals(luckNumberMessageId)) {
    System.out.printf("Lucky number 5 id %s, message %s: \n", msg.getMessageId(), response);
    consumer.acknowledge(msg);
}
```

I borrowed most of this from http://pulsar.incubator.apache.org/docs/latest/clients/Java/.
Hope this helps anyone looking for a short "how-to".
