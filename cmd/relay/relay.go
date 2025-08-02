package main

import (
	"github.com/evernetlabs/evernet/internal/app/relay"
	"github.com/evernetlabs/evernet/internal/pkg/env"
)

func main() {
	relay.NewRelay(&relay.Config{
		Host: env.GetOrDefault("HOST", "0.0.0.0"),
		Port: env.GetOrDefault("PORT", "5000"),
	}).Start()
}
