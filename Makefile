SERVER=src/server/main
CLIENT=src/client/main
COMP=kotlinc
ARG=-include-runtime -d

INTERFACES=src/interfaces/IO.kt
SERVER_ENTITIES=src/server/entities/Server.kt
CLIENT_ENTITIES=src/client/entities/Client.kt

all:
	make compServer
	make compClient

compServer:
	$(COMP) $(SERVER).kt $(INTERFACES) $(SERVER_ENTITIES) $(ARG) $(SERVER).jar

compClient:
	$(COMP) $(CLIENT).kt $(INTERFACES) $(CLIENT_ENTITIES) $(ARG) $(CLIENT).jar

runS:
	java -jar $(SERVER).jar

runC:
	java -jar $(CLIENT).jar

clean:
	rm {$(SERVER),$(CLIENT)}.jar
