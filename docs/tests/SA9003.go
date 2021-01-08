//#Patterns: SA9003
package main

import "fmt"

func main() {

    if 7%2 == 0 {
        fmt.Println("potato")
        //#Info: SA9003
    } else { }
}
