all: server client

server:
	cd cmd/server && go build -o ../../bin/ghs

client:
	cd cmd/client && go build -o ../../bin/ghc

generate:
	go generate ./...
