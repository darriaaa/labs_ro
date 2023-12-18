package main

import (
	"fmt"
	"math"
	"sync"
	"time"
)

// Edge представляє рейс між двома містами
type Edge struct {
	Source      string
	Destination string
	Price       float64
}

// Graph представляє граф автобусних рейсів
type Graph struct {
	mu     sync.RWMutex
	cities map[string]bool
	edges  map[Edge]float64
}

// NewGraph створює новий граф
func NewGraph() *Graph {
	return &Graph{
		cities: make(map[string]bool),
		edges:  make(map[Edge]float64),
	}
}

// AddCity додає нове місто до графу
func (g *Graph) AddCity(city string) {
	g.mu.Lock()
	defer g.mu.Unlock()
	g.cities[city] = true
}

// RemoveCity видаляє місто з графу
func (g *Graph) RemoveCity(city string) {
	g.mu.Lock()
	defer g.mu.Unlock()
	delete(g.cities, city)

	// Видаляє всі рейси, пов'язані з цим містом
	for edge := range g.edges {
		if edge.Source == city || edge.Destination == city {
			delete(g.edges, edge)
		}
	}
}

// AddEdge додає новий рейс між містами
func (g *Graph) AddEdge(source, destination string, price float64) {
	g.mu.Lock()
	defer g.mu.Unlock()
	g.edges[Edge{Source: source, Destination: destination}] = price
	g.edges[Edge{Source: destination, Destination: source}] = price
}

// RemoveEdge видаляє рейс між містами
func (g *Graph) RemoveEdge(source, destination string) {
	g.mu.Lock()
	defer g.mu.Unlock()
	delete(g.edges, Edge{Source: source, Destination: destination})
	delete(g.edges, Edge{Source: destination, Destination: source})
}

// ChangeTicketPrice змінює ціну квитка на рейсі між містами
func (g *Graph) ChangeTicketPrice(source, destination string, newPrice float64) {
	g.mu.Lock()
	defer g.mu.Unlock()
	g.edges[Edge{Source: source, Destination: destination}] = newPrice
	g.edges[Edge{Source: destination, Destination: source}] = newPrice
}

// FindPath знаходить шлях із міста source до міста destination та обчислює ціну поїздки
func (g *Graph) FindPath(source, destination string) (float64, []string) {
	g.mu.RLock()
	defer g.mu.RUnlock()

	visited := make(map[string]bool)
	path := []string{source}
	var totalCost float64

	// Рекурсивна функція для знаходження шляху
	var dfs func(currentCity string) bool
	dfs = func(currentCity string) bool {
		visited[currentCity] = true

		if currentCity == destination {
			return true
		}

		for city := range g.cities {
			if !visited[city] {
				if price, ok := g.edges[Edge{Source: currentCity, Destination: city}]; ok {
					path = append(path, city)
					totalCost += price

					if dfs(city) {
						return true
					}

					// Якщо шлях не знайдено, відкатуємо зміни
					path = path[:len(path)-1]
					totalCost -= price
				}
			}
		}

		return false
	}

	if dfs(source) {
		return totalCost, path
	}

	return math.Inf(1), nil
}

func main() {
	graph := NewGraph()

	graph.AddCity("A")
	graph.AddCity("B")
	graph.AddCity("C")

	graph.AddEdge("A", "B", 10.0)
	graph.AddEdge("B", "C", 15.0)

	fmt.Println("Граф до змін:")
	fmt.Println("Міста:", graph.cities)
	fmt.Println("Рейси:", graph.edges)

	// Потік, що змінює ціну квитка
	go func() {
		graph.ChangeTicketPrice("A", "B", 20.0)
	}()

	// Потік, що видаляє і додає рейси між містами
	go func() {
		graph.RemoveEdge("A", "B")
		graph.AddEdge("A", "C", 25.0)
	}()

	// Потік, що видаляє старі міста і додає нові
	go func() {
		graph.RemoveCity("C")
		graph.AddCity("D")
	}()

	// Чекаємо, поки всі потоки завершаться
	time.Sleep(time.Second)

	fmt.Println("\nГраф після змін:")
	fmt.Println("Міста:", graph.cities)
	fmt.Println("Рейси:", graph.edges)

	// Потік, що визначає шлях та ціну поїздки
	go func() {
		cost, path := graph.FindPath("A", "D")
		if path != nil {
			fmt.Printf("\nШлях з A до D: %v\nЗагальна вартість: %f\n", path, cost)
		} else {
			fmt.Println("\nШлях з A до D не знайдено.")
		}
	}()

	// Чекаємо, поки останній потік завершиться
	time.Sleep(time.Second)
}
