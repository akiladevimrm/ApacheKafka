# Apache Kafka Installation using Docker

Install and run the Apache Kafka application using Docker Compose. 

# Pre-requisites:
```install docker-compose https://docs.docker.com/compose/install/```
The main folder contains docker-compose.yml file. If you want to customize any Kafka parameters, simply add them as environment variables in docker-compose.yml.

# why use wurstmeister images?
- All images are built with the recommended version of scala documented on http://kafka.apache.org/downloads.
- With wurstmeister images the latest bug fixes and features are available as soon as possible.
- wurstmeister containers, virtual machines and cloud images use the same components and configuration approach - making it easy to switch between formats based on your project needs.

# Usage
Run the following command as below:
```
$ docker-compose -f docker-compose.yml up -d
```
It starts the zookeeper and kafka server as a seperate containers in your host operating systems.

# Setup Authentication in Kafka
Once the kafka container started, login to the server and navigate to the config folder to update the following properties file.
```
$docker exec -it <Kafka-Container-Name> bash
#cd /opt/kafka/config
```
# server.properties
```
security.inter.broker.protocol=SASL_PLAINTEXT
sasl.mechanism.inter.broker.protocol=PLAIN
sasl.enabled.mechanisms=PLAIN
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
allow.everyone.if.no.acl.found=true
auto.create.topics.enable=false
broker.id=0
listeners=SASL_PLAINTEXT://localhost:9092
advertised.listeners=SASL_PLAINTEXT://localhost:9092
num.network.threads=3
num.io.threads=8
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600
advertised.host.name=localhost
num.partitions=1
num.recovery.threads.per.data.dir=1
log.flush.interval.messages=30000000
log.flush.interval.ms=1800000
log.retention.minutes=30
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000
delete.topic.enable=true
zookeeper.connect=localhost:2181
zookeeper.connection.timeout.ms=6000
super.users=User:admin
```
# zookeeper.properties
```
dataDir=/tmp/zookeeper
clientPort=2181
maxClientCnxns=0
authProvider.1=org.apache.zookeeper.server.auth.SASLAuthenticationProvider
requireClientAuthScheme=sasl
jaasLoginRenew=3600000
```

# producer.properties
```
security.protocol=SASL_PLAINTEXT
sasl.mechanism=PLAIN
bootstrap.servers=localhost:9092
compression.type=none
```

# consumer.properties
```
security.protocol=SASL_PLAINTEXT
sasl.mechanism=PLAIN
zookeeper.connect=localhost:2181
zookeeper.connection.timeout.ms=6000
group.id=test-consumer-group
```

# Create zookeeper_jaas.conf
```
Server {
   org.apache.kafka.common.security.plain.PlainLoginModule required
   username="admin"
   password="admin-secret"
   user_admin="admin-secret";
};
```
# Create kafka_server_jaas.conf
```
KafkaServer {
   org.apache.kafka.common.security.plain.PlainLoginModule required
   username="admin"
   password="admin-secret"
   user_admin="admin-secret";
};

Client {
   org.apache.kafka.common.security.plain.PlainLoginModule required
   username="admin"
   password="admin-secret";
};
```
After doing all these configuration, on a first terminal window: Restart zookeeper server from Kafka root directory
# Terminal1
```
$ export KAFKA_OPTS="-Djava.security.auth.login.config=/opt/kafka/config/zookeeper_jaas.conf"
$ ./bin/zookeeper-server-start.sh config/zookeeper.properties
```

# Terminal2
From Kafka root directory, restart kafka server
```
$ export KAFKA_OPTS="-Djava.security.auth.login.config=/opt/kafka/config/kafka_server_jaas.conf"
$ ./bin/kafka-server-start.sh config/server.properties
```

Once the servers are started, you can produce and subscribe the message. Unfortunately i am facing issue in starting kafka server, getting the below exception.
> [2020-03-15 17:33:47,483] INFO [ZooKeeperClient Kafka server] Waiting until connected. (kafka.zookeeper.ZooKeeperClient)
> [2020-03-15 17:33:47,678] INFO Client successfully logged in. (org.apache.zookeeper.Login)
> [2020-03-15 17:33:47,684] INFO Client will use DIGEST-MD5 as SASL mechanism. (org.apache.zookeeper.client.ZooKeeperSaslClient)
> [2020-03-15 17:33:47,697] INFO Opening socket connection to server localhost/127.0.0.1:2181. Will attempt to SASL-authenticate using Login Context section 'Client' (org.apache.zookeeper.ClientCnxn)
> [2020-03-15 17:33:47,723] INFO Socket connection established, initiating session, client: /127.0.0.1:39374, server: localhost/127.0.0.1:2181 (org.apache.zookeeper.ClientCnxn)
> [2020-03-15 17:33:47,860] INFO Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x10002042c510000, negotiated timeout = 6000 (org.apache.zookeeper.ClientCnxn)
> [2020-03-15 17:33:47,912] INFO [ZooKeeperClient Kafka server] Connected. (kafka.zookeeper.ZooKeeperClient)
> [2020-03-15 17:33:48,746] INFO Cluster ID = jJk5poP3TCG1hSlsmVQXFw (kafka.server.KafkaServer)
> [2020-03-15 17:33:48,783] ERROR Fatal error during KafkaServer startup. Prepare to shutdown (kafka.server.KafkaServer)
> kafka.common.InconsistentClusterIdException: The Cluster ID jJk5poP3TCG1hSlsmVQXFw doesn't match stored clusterId Some(Rk4oiqtISnilHA5hIPlpeg) in meta.properties. The broker is trying to join the wrong cluster. Configured zookeeper.connect may be wrong.
        at kafka.server.KafkaServer.startup(KafkaServer.scala:220)
        at kafka.server.KafkaServerStartable.startup(KafkaServerStartable.scala:44)
        at kafka.Kafka$.main(Kafka.scala:84)
        at kafka.Kafka.main(Kafka.scala)
> [2020-03-15 17:33:48,815] INFO shutting down (kafka.server.KafkaServer)
> [2020-03-15 17:33:48,843] INFO [ZooKeeperClient Kafka server] Closing. (kafka.zookeeper.ZooKeeperClient)
> [2020-03-15 17:33:48,955] INFO Session: 0x10002042c510000 closed (org.apache.zookeeper.ZooKeeper)
> [2020-03-15 17:33:48,955] INFO EventThread shut down for session: 0x10002042c510000 (org.apache.zookeeper.ClientCnxn)
[2020-03-15 17:33:48,963] INFO [ZooKeeperClient Kafka server] Closed. (kafka.zookeeper.ZooKeeperClient)
> [2020-03-15 17:33:49,038] INFO shut down completed (kafka.server.KafkaServer)
> [2020-03-15 17:33:49,041] ERROR Exiting Kafka. (kafka.server.KafkaServerStartable)
> [2020-03-15 17:33:49,102] INFO shutting down (kafka.server.KafkaServer)

# Issue
  - Renamed the meta.properties inside the folder 
> /kafka/kafka-logs-196d75584134

Post that getting a different exception stating that the folder /kafka/kafka-logs-196d75584134  is locked by a different thread.
