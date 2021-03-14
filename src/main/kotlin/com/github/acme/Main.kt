package com.github.acme

import com.github.acme.MeasurementUnits.Gram
import com.github.acme.MeasurementUnits.Millilitre

infix fun Double.to(unit: MeasurementUnit): Quantity = Quantity.of(this, unit)

// Entry-point in your app
fun main() {
    val milkReservoir = FixedSizeTankLiquidSourceV2(1000.0 to Millilitre)
    val waterSource = TapLiquidSourceV2()
    val coffeeContainer = CoffeeContainerV2(500.0 to Gram)

    val myOwnMachine = CoffeeMakerV4(
            milkSource = milkReservoir,
            waterSource = waterSource,
            coffeeContainer = coffeeContainer,
            recipes = RecipeLoader.fromYaml(readFile("recipes.yaml")),
            toppings = ToppingLoader.fromYaml(readFile("toppings.yaml"))
    )

    milkReservoir.topUp(1000.0 to Millilitre)
    coffeeContainer.topUp(1000.0 to Gram)

    val coffee = myOwnMachine.brew("medium latte machiatto with espresso and whip cream")
    println(coffee)
}

private fun readFile(fileName: String) = CoffeeMakerV4::class.java.classLoader.getResourceAsStream(fileName)!!
