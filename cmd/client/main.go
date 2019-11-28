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
	"flag"
	"fmt"
	"net"
	"strconv"
	"time"

	"github.com/exploratoryengineering/gyorde/pkg/gyorde"
	"google.golang.org/grpc"
)

const defaultEndpoint = "127.0.0.1:1234"

func main() {
	endpoint := flag.String("endpoint", defaultEndpoint, "Endpoint for server")
	imsiStr := flag.String("imsi", "99912345678995", "IMSI to check")
	ipStr := flag.String("ip", "127.0.0.1", "IP for device")

	flag.Parse()

	ip := net.ParseIP(*ipStr)
	ipbuf := ip.To16()

	ipType := gyorde.CheckDeviceRequest_IPV6
	if ip.To16() != nil {
		ipType = gyorde.CheckDeviceRequest_IPV4
		ipbuf = ip.To4()
	}

	if ipbuf == nil {
		fmt.Println("Invalid IP: %s\n", *ipStr)
		return
	}

	imsi, err := strconv.ParseInt(*imsiStr, 10, 63)
	if err != nil {
		fmt.Println("Invalid IMSI: %v\n", *imsiStr)
	}

	conn, err := grpc.Dial(*endpoint, grpc.WithInsecure())
	if err != nil {
		panic(err)
	}

	defer conn.Close()

	client := gyorde.NewDeviceCheckClient(conn)

	ctx, done := context.WithTimeout(context.Background(), 1*time.Second)
	defer done()

	resp, err := client.CheckDevice(ctx, &gyorde.CheckDeviceRequest{
		Imsi:      imsi,
		IpType:    ipType,
		IpAddress: ipbuf,
	})
	if err != nil {
		fmt.Printf("Error calling server: %v\n", err)
		return
	}
	fmt.Printf("Response: %+v\n", resp)
}
