package main

//
//Copyright 2019 Telenor Digital AS
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//
import (
	"context"
	"errors"
	"fmt"
	"net"

	"github.com/exploratoryengineering/gyorde/pkg/gyorde"
	"google.golang.org/grpc"
)

func startDummyServer(endpoint string, imsis []int64) {
	dummyServer := &gyordeDummyServer{imsis: imsis}

	server := grpc.NewServer()
	gyorde.RegisterDeviceCheckServer(server, dummyServer)

	listener, err := net.Listen("tcp", endpoint)
	if err != nil {
		panic(err)
	}

	fmt.Println("Listening on ", endpoint)
	if err := server.Serve(listener); err != nil {
		panic(err)
	}
}

type gyordeDummyServer struct {
	imsis []int64
}

func (d *gyordeDummyServer) CheckDevice(ctx context.Context, req *gyorde.CheckDeviceRequest) (*gyorde.CheckDeviceResponse, error) {
	if d.imsis == nil || len(d.imsis) == 0 {
		return nil, errors.New("no imsis provided")
	}
	for _, v := range d.imsis {
		if v == req.Imsi {
			fmt.Println("Accepting IMSI", req.Imsi)
			return &gyorde.CheckDeviceResponse{Success: true}, nil
		}
	}
	fmt.Println("Rejecting IMSI", req.Imsi)
	return &gyorde.CheckDeviceResponse{Success: false, ErrorMessage: "Unknown IMSI"}, nil
}
