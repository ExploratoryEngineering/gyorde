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
	"bufio"
	"fmt"
	"io"
	"os"
	"strconv"
	"strings"
)

const imsiFile = "imsi.txt"
const endpoint = ":1234"

func main() {

	imsis := make([]int64, 0)
	f, err := os.Open(imsiFile)
	if err != nil {
		panic(err)
	}
	defer f.Close()

	r := bufio.NewReader(f)
	for {
		imsiStr, err := r.ReadString('\n')
		if err == io.EOF {
			break
		}
		if err != nil {
			panic(err)
		}
		if strings.HasPrefix(imsiStr, "#") {
			continue
		}
		val, err := strconv.ParseInt(strings.TrimSpace(imsiStr), 10, 63)
		if err != nil {
			fmt.Println("Invalid IMSI:", imsiStr)
			return
		}
		imsis = append(imsis, val)
	}
	fmt.Printf("%d IMSIs read from file\n", len(imsis))
	startDummyServer(endpoint, imsis)
}
