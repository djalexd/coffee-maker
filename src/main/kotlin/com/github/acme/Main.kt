package com.github.acme

// Entry-point in your app
fun main() {
    val myOwnMachine = CoffeeMaker()
    myOwnMachine.addCoffee(200)
    myOwnMachine.addMilk(90)
    myOwnMachine.addWater(500)
    println(myOwnMachine.toString())
    Thread.sleep(5)
    println("We just made a: ${myOwnMachine.makeMilkFoam(2)}")
    println(myOwnMachine.toString())
    var brewed = myOwnMachine.brew("cappuccino")
    println("We just made a: ${brewed.toString()}")
    brewed = myOwnMachine.brew("espresso")
    println("We just made a: ${brewed.toString()}")
    println(myOwnMachine.toString())
}