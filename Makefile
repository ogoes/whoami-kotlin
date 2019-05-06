SERVER=src/server/main
CLIENT=src/client/main
COMP=kotlinc
ARG=-include-runtime -d

INTERFACES=src/interfaces/IO.kt
SERVER_ENTITIES=src/server/entities/Server.kt
CLIENT_ENTITIES=src/client/entities/Client.kt





all: compServer compClient
	1 2> /dev/null

runS: compServer
	java -jar $(SERVER).jar

runC: compClient
	java -jar $(CLIENT).jar localhost:9999

compServer:
	$(COMP) $(SERVER).kt $(INTERFACES) $(CLIENT_ENTITIES) $(SERVER_ENTITIES) $(ARG) $(SERVER).jar

compClient:
	$(COMP) $(CLIENT).kt $(INTERFACES) $(CLIENT_ENTITIES) $(ARG) $(CLIENT).jar



test:
	$(COMP) teste.kt $(ARG) teste.jar && java -jar teste.jar

clean:
	rm {$(SERVER),$(CLIENT)}.jar

