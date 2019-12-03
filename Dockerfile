# Dockerfile References: https://docs.docker.com/engine/reference/builder/

# Start from the latest golang base image
FROM golang:latest

# Add Maintainer Info
LABEL maintainer="Bjørn Remseth <la3lma@gmail.com>"

# Set the Current Working Directory inside the container
WORKDIR /app

# Copy go mod and sum files
COPY go.mod go.sum ./

# Download all dependencies. Dependencies will be cached if the go.mod and go.sum files are not changed
RUN go mod download

# Copy the source from the current directory to the Working Directory inside the container
# Dockerfile	Dockerfile~	LICENSE		Makefile	README.md	TODO.txt	bin		cmd		go.mod		go.sum		imsi.txt	pkg		protobuf

COPY go.sum .
COPY pkg .
COPY protobuf .


# Build the gyorde server
# RUN go build -o main .

# RUN mkdir /app
RUN go get -u github.com/golang/protobuf/protoc-gen-go
RUN PATH="$PATH:$GOPATH/bin" go generate ./...
WORKDIR cmd/server
RUN go build -o ../../app

# Expose port 9111  (default grpc port) to the outside world
EXPOSE 9111

# Command to run the executable
CMD ["./app/ghs"]
