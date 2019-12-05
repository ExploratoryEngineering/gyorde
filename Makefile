all: generate server client java

server:
	cd cmd/server && go build -o ../../bin/ghs

client:
	cd cmd/client && go build -o ../../bin/ghc

java:
	gradle build

generate:
	go generate ./...
