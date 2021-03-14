package com.github.acme

import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Test
import kotlin.math.exp

class CoffeeMakerV2Test {

    @Test
    fun `test new recipes work`() {
        val coffeeMakerV2 = createSomeCoffeeMaker(100, 100, 100, listOf(latte))

        val brew = coffeeMakerV2.brew("latte")
        Assert.assertNotNull(brew)

    }

    @Test
    fun `test unknown recipe doesn't work`() {
        val coffeeMakerV2 = createSomeCoffeeMaker()

        val brew = coffeeMakerV2.brew("latte")
        Assert.assertNull(brew)
    }

    @Test
    fun `test that coffee machine correctly substracts ingredients`() {
        val coffeeMakerV2 = createSomeCoffeeMaker(40, 60, 60, listOf(latte))

        coffeeMakerV2.brew("latte")

        Assert.assertEquals(40, coffeeMakerV2.coffeeQuantityInGrams)
        Assert.assertEquals(0, coffeeMakerV2.waterQuantityInMl)
        Assert.assertEquals(0, coffeeMakerV2.milkQuantityInMl)

    }

    @Test(expected = NotEnoughCoffeeException::class)
    fun `test that coffee machine throws error if not enough coffee`() {
        val coffeeMakerV2 = createSomeCoffeeMaker(40, null, 15, listOf(espresso))
        coffeeMakerV2.brew("espresso")
    }

    @Test(expected = NotEnoughMilkException::class)
    fun `test that coffee machine throws error if not enough milk`() {
        val coffeeMakerV2 = createSomeCoffeeMaker(40, 50, 20, listOf(latte))
        coffeeMakerV2.brew("latte")
    }

    @Test(expected = NotEnoughWaterException::class)
    fun `test that coffee machine throws error if not enough water`() {
        val coffeeMakerV2 =  createSomeCoffeeMaker(9, 20, 100, listOf(latte))
        coffeeMakerV2.brew("latte")
    }

    @Test
    fun `test that coffee maker displays the available ingredients`() {
        val coffeeMakerV2 = createSomeCoffeeMaker(20, 40, 60, listOf(americano))

        val brew = coffeeMakerV2.toString()

        println(brew)
    }

    @Test
    fun `test that coffee maker can't brew unknown recipe although there is one existing`() {
        val coffeeMakerV2 = createSomeCoffeeMaker(20, 20, 60, listOf(latte))
        val brew = coffeeMakerV2.brew("espresso")
        Assert.assertNull(brew)
    }


    fun createRecipe(
            name: String,
            coffeeQuantityInGrams: Int,
            waterQuantityInMl: Int,
            milkQuantityInMl: Int) = Recipe(name, coffeeQuantityInGrams, waterQuantityInMl, milkQuantityInMl) { CoffeeBrew(name) }

    fun createSomeCoffeeMaker(
            waterQuantityInMl: Int? = null,
            milkQuantityInMl: Int? = null,
            coffeeInGrams: Int? = null,
            listOfRecipes: List<Recipe> = emptyList()
    ): CoffeeMakerV2 {

        val coffeeMakerV2 = CoffeeMakerV2(
                listOfRecipes
        )
        if (waterQuantityInMl != null) {
            coffeeMakerV2.addWater(waterQuantityInMl)
        }

        if (milkQuantityInMl != null) {
            coffeeMakerV2.addMilk(milkQuantityInMl)
        }

        if (coffeeInGrams != null) {
            coffeeMakerV2.addCoffee(coffeeInGrams)
        }
        return coffeeMakerV2
    }

    val espresso = createRecipe("espresso", 20, 10, 0)
    val latte = createRecipe("latte", 20, 40, 60)
    val americano = createRecipe("americano", 20, 40, 60)

}