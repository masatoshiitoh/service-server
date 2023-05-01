package org.apache.camel.learn;

import io.vertx.core.impl.VertxBuilder;
import io.vertx.core.impl.transports.JDKTransport;
import io.vertx.core.spi.ExecutorServiceFactory;
import io.vertx.core.spi.VertxThreadFactory;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaConfiguration;
import org.apache.camel.component.vertx.VertxComponent;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {
        VertxComponent vertxComponent = new VertxComponent();
        vertxComponent.setVertxFactory(
                new VertxBuilder()
                        .threadFactory(VertxThreadFactory.INSTANCE)
                        .findTransport(new JDKTransport())
                        .executorServiceFactory(ExecutorServiceFactory.INSTANCE)
                        .clusterManager(new HazelcastClusterManager())

        );
        vertxComponent.setHost("hostname");
        getCamelContext().addComponent("vertx", vertxComponent);

        KafkaComponent kafkaComponent = new KafkaComponent();
        KafkaConfiguration kafkaConfiguration = new KafkaConfiguration();
        kafkaConfiguration.setBrokers("192.168.86.102:9092");
        kafkaComponent.setConfiguration(kafkaConfiguration);

        getCamelContext().addComponent("kafka", kafkaComponent);
        // here is a sample which processes the input files
        // (leaving them in place - see the 'noop' flag)
        // then performs content based routing on the message using XPath
        from("file:src/data?noop=true")
            .choice()
                .when(xpath("/person/city = 'London'"))
                    .log("UK message")
                    .to("file:target/messages/uk")
                .otherwise()
                    .log("Other message")
                    .to("file:target/messages/others");
    }

}
