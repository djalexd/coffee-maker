package com.github.acme

import org.junit.Assert
import org.junit.Test

class CoffeeMakerV1Test {

    @Test
    fun `test that coffee maker displays the quantities`() {
        //GIVEN
        val coffeeMakerV1 = CoffeeMakerV1()
        //WHEN
        println(coffeeMakerV1.toString())
        //THEN

    }

    @Test(expected = NotEnoughWaterException::class)
    fun `test that coffee maker returns not enough water exception when making espresso`() {
        val coffeeMakerV1 = CoffeeMakerV1()
        coffeeMakerV1.brew("espresso")
    }

    @Test(expected = NotEnoughCoffeeException::class)
    fun `test that coffee maker returns not enough coffee exception`() {
        val coffeeMakerV1 = CoffeeMakerV1()
        coffeeMakerV1.addWater(100)
        coffeeMakerV1.brew("espresso")
    }

    @Test
    fun `test that ingredients are correctly substracted when making espresso`() {
        val coffeeMakerV1 = CoffeeMakerV1()
        coffeeMakerV1.addWater(100)
        coffeeMakerV1.addCoffee(100)
        Assert.assertNotNull(coffeeMakerV1.brew("espresso"))
        Assert.assertEquals(70, coffeeMakerV1.waterQuantityInMl)
        Assert.assertEquals(50, coffeeMakerV1.coffeeQuantityInGrams)
    }

    @Test(expected = NotEnoughWaterException::class)
    fun `test that coffee maker returns not enough water exception when making cappuccino `() {
        val coffeeMakerV1 = CoffeeMakerV1()
        coffeeMakerV1.brew("cappuccino")
    }

    @Test(expected = NotEnoughCoffeeException::class)
    fun `test that coffee maker returns not enough coffee exception when making cappuccino`() {
        val coffeeMakerV1 = CoffeeMakerV1()
        coffeeMakerV1.addWater(100)
        coffeeMakerV1.brew("cappuccino")
    }

    @Test(expected = NotEnoughMilkException::class)
    fun `test that coffee maker returns not enough milk exception when making cappuccino`() {
        val coffeeMakerV1 = createSomeCoffeeMaker(100, null, 100)
        coffeeMakerV1.brew("cappuccino")
    }

    @Test
    fun `test that coffee maker substracts all ingredients when making cappuccino`() {
        // given
        val coffeeMakerV1 = createSomeCoffeeMaker(100, 100, 100)
        // when
        coffeeMakerV1.brew("cappuccino")
        // then
        Assert.assertEquals(50, coffeeMakerV1.waterQuantityInMl)
        Assert.assertEquals(50, coffeeMakerV1.coffeeQuantityInGrams)
        Assert.assertEquals(70, coffeeMakerV1.milkQuantityInMl)
    }

    @Test
    fun `test that coffee maker doesn't brew unknown reicpes`() {
        val coffeeMakerV1 = CoffeeMakerV1()
        val brew = coffeeMakerV1.brew("Latte")
        Assert.assertNull(brew)
    }



    private fun createSomeCoffeeMaker(
            waterQuantityInMl: Int? = null,
            milkQuantityInMl: Int? = null,
            coffeeInGrams: Int? = null
    ): CoffeeMakerV1 {
        val coffeeMakerV1 = CoffeeMakerV1()
        if (waterQuantityInMl != null) {
            coffeeMakerV1.addWater(waterQuantityInMl)
        }
        if (coffeeInGrams != null) {
            coffeeMakerV1.addCoffee(coffeeInGrams)
        }
        if (milkQuantityInMl != null) {
            coffeeMakerV1.addMilk(milkQuantityInMl)
        }
        return coffeeMakerV1
    }


}

