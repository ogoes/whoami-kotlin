ARQ1=client
ARQ2=server
COMP=kotlinc
ARG=-include-runtime -d

all:
	$(COMP) $(ARQ1).kt $(ARG) $(ARQ1).jar
	$(COMP) $(ARQ2).kt $(ARG) $(ARQ2).jar
runS:
	java -jar $(ARQ2).jar
runC:
	java -jar $(ARQ1).jar
clean:
	rm *.jar