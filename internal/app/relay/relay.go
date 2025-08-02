package relay

import (
	"bufio"
	"fmt"
	"log"
	"net"
)

type Relay struct {
	config *Config
}

func NewRelay(config *Config) *Relay {
	return &Relay{config: config}
}

type Config struct {
	Host string
	Port string
}

func (r *Relay) Start() {
	ln, err := net.Listen("tcp", fmt.Sprintf("%s:%s", r.config.Host, r.config.Port))
	if err != nil {
		panic(err)
	}
	defer func(ln net.Listener) {
		err := ln.Close()
		if err != nil {
			log.Fatal("error closing listener: ", err)
		}
	}(ln)

	log.Printf("server is listening on %s:%s", r.config.Host, r.config.Port)

	for {
		conn, err := ln.Accept()
		if err != nil {
			log.Print("Accept error:", err)
			continue
		}

		go handleConnection(conn)
	}
}

func handleConnection(conn net.Conn) {
	defer func(conn net.Conn) {
		err := conn.Close()
		if err != nil {
			log.Print("error closing connection: ", err)
		}
	}(conn)

	log.Print("client connected: ", conn.RemoteAddr())

	scanner := bufio.NewScanner(conn)

	for scanner.Scan() {
		text := scanner.Text()
		log.Print("received message: ", text)

		// Echo the message back
		_, err := conn.Write([]byte("echo: " + text + "\n"))
		if err != nil {
			log.Print("error sending message: ", err)
			return
		}
	}

	if err := scanner.Err(); err != nil {
		log.Print("error scanning input: ", err)
	}

	log.Print("client disconnected:", conn.RemoteAddr())
}
