# testwar

mvn dependency:resolve


java -jar testhollowjar.jar --install-dir=wildfly --deployment=testwar.war


mvn -B verify -Dgroups="MicroShedTest" -DexcludedGroups="IntegrationTest,SmokeTest,LoadTest"



docker run -e POSTGRES_PASSWORD=postgres -p 5432:15432 postgres:13

mvn flyway:info
mvn flyway:migrate

java -jar ../testhollowjar/target/testhollowjar.jar --deployment=target/testmessagingwar.war -Djboss.socket.binding.port-offset=100

http://localhost:9990/health
http://localhost:9990/console/index.html

http://localhost:8080/hello
http://localhost:8080/test/1