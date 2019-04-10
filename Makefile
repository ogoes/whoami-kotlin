ARQ1=server
ARQ2=client
COMP=kotlinc
ARG=-include-runtime -d

all:
	make compServer
	make compClient

compServer:
	$(COMP) $(ARQ1).kt $(ARG) $(ARQ1).jar

compClient:
	$(COMP) $(ARQ2).kt $(ARG) $(ARQ2).jar

runS:
	java -jar $(ARQ2).jar

runC:
	java -jar $(ARQ1).jar

clean:
	rm *.jar