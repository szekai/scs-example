$ KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
$ bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/kraft/server.properties
$ bin/kafka-server-start.sh config/kraft/server.properties

bin/kafka-topics.sh --create --topic orderStatus --bootstrap-server 192.168.1.112:9092
bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server 192.168.1.112:9092
bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server 192.168.1.112:9092

bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server 192.168.1.112:9092

bin/kafka-topics.sh --list --bootstrap-server 192.168.1.112:9092