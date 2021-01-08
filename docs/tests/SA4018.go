//#Patterns: SA4018
package main

func c() {
    c := 1
    //#Info: SA4018
    c = c
   _ = c
}
