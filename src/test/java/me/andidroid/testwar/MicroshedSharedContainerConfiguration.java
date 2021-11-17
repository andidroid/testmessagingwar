package me.andidroid.testwar;

import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.output.MigrateResult;
import org.flywaydb.core.api.configuration.ClassicConfiguration;

public class MicroshedSharedContainerConfiguration implements SharedContainerConfiguration
{
        /**
         * Logging via slf4j api
         */
        private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MicroshedSharedContainerConfiguration.class);
        
        // @Container
        // public static MockServerContainer mockServer = new MockServerContainer().withNetworkAliases("mockserver");
        
        // @Container
        // public static GenericContainer greenMailContainer = new GenericContainer<>(DockerImageName.parse("greenmail/standalone:1.6.5")).waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1)).withNetworkAliases("mygreenmail")
        // .withEnv("GREENMAIL_OPTS", "-Dgreenmail.setup.test.smtps -Dgreenmail.setup.test.api -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.users=abutest693@gmail.com:test").withLogConsumer(new Slf4jLogConsumer(LOGGER))
        // // .withReuse(true)
        // .withExposedPorts(3465);// 8080
        
        // DockerImageName.parse("confluentinc/cp-kafka:latest")
        @Container
        public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest")).withNetworkAliases("mykafka");
        
        @Container
        public static ApplicationContainer app = new ApplicationContainer()
                        
                        .withEnv("POSTGRESQL_JNDI", "java:jboss/datasources/PostgreSQLDS").withEnv("POSTGRESQL_HOST", "mypostgres").withEnv("POSTGRESQL_PORT", "5432").withEnv("POSTGRESQL_USER", "postgres").withEnv("POSTGRESQL_PASSWORD", "postgres")
                        .withEnv("POSTGRESQL_DATABASE", "test").withEnv("message", "Hello World from MicroShed Testing")
                        // jgroups
                        .withEnv("JGROUPS_CLUSTER_PASSWORD", "test")
                        // mail
                        .withEnv("MAIL_USERNAME", "abutest693@gmail.com").withEnv("MAIL_PASSWORD", "test").withEnv("test.mail", "abutest693@gmail.com").withEnv("MAIL_HOST", "mygreenmail").withEnv("MAIL_PORT", "3465")
                        // kafka mp.messaging.connector.smallrye-kafka.bootstrap.servers
                        .withEnv("MP_MESSAGING_CONNECTOR_SMALLRYE_KAFKA_BOOTSTRAP_SERVERS", "mykafka:9092")
                        // readiness
                        .withReadinessPath("/testservice/hello/hello")
                        // other containers
                        .dependsOn( /* greenMailContainer, */ kafkaContainer);
        // .withAppContextRoot("/testwar")
        ;
        // .withMpRestClient(QuoteRestClient.class,"http://mockserver:"+MockServerContainer.PORT);
        
        @Override
        public void startContainers()
        {
                
                System.out.println("startContainers");
                
                // greenMailContainer.start();
                
                app.start();
                System.out.println("app started");
                System.out.println("startContainers finished");
                
                // super.startContainers();
        }
        
}
