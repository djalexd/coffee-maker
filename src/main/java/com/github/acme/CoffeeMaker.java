package com.github.acme;

import java.util.List;

/**
 * Make me some coffee!
 */
public class CoffeeMaker {
	
	private int milkQuantityInMl = 0;
	private int waterQuantityInMl = 0;
	private int coffeeQuantityInGrams = 0;

	List<String> getCoffeeMenu() {
		return null;
	}
	
	/**
	 * 
	 * @param coffeeType
	 * @return Will return null, if coffeeType is not 'espresso' or 'cappuccino'
	 * @throws NotEnoughWaterException
	 * @throws NotEnoughCoffeeException
	 * @throws NotEnoughMilkException
	 */
	CoffeeBrew brew(String coffeeType) throws NotEnoughWaterException, NotEnoughCoffeeException, NotEnoughMilkException {
		if (coffeeType.equals("espresso")) {
			
			// espresso   [water 30ml, coffee 50g]
			if (waterQuantityInMl < 30) {
				throw new NotEnoughWaterException();
			}
			
			if (coffeeQuantityInGrams < 50) {
				throw new NotEnoughCoffeeException();
			}
			
			waterQuantityInMl -= 30;
			coffeeQuantityInGrams -= 50;
			
			return new Espresso();
			
		} else if (coffeeType.equals("cappuccino")) {
			
			// cappuccino [water 50ml, coffee 50g, milk 30ml]
			if (waterQuantityInMl < 50) {
				throw new NotEnoughWaterException();
			}

			if (coffeeQuantityInGrams < 50) {
				throw new NotEnoughCoffeeException();
			}
			
			if (milkQuantityInMl < 30) {
				throw new NotEnoughMilkException();
			}

			waterQuantityInMl -= 50;
			coffeeQuantityInGrams -= 50;
			milkQuantityInMl -= 30;
			
			return new Cappuccino();
			
		} else {
			return null;
		}
	}
	
	MilkFoam makeMilkFoam(int numberOfSeconds) throws NotEnoughMilkException {
		int quantity = (int) (numberOfSeconds * 5.5);
		if (milkQuantityInMl < quantity) {
			throw new NotEnoughMilkException();
		} else {
			milkQuantityInMl -= quantity;
			return new MilkFoam();
		}
	}
	
	void cleanItself() throws NotEnoughWaterException {}
	
	void addCoffee(int quantityInGrams) {
		//int currentQuantity = coffeeQuantityInGrams;
		//coffeeQuantityInGrams = currentQuantity + quantityInGrams;
		coffeeQuantityInGrams += quantityInGrams;
	}
	
	void addMilk(int quantityInMl) {
		milkQuantityInMl += quantityInMl;
	}
	
	void addWater(int quantityInMl) {
		waterQuantityInMl += quantityInMl;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("CoffeeMaker CO:\n");
		b.append("\tavailable water: ").append(waterQuantityInMl).append(" ml").append("\n");
		b.append("\tavailable coffee: ").append(coffeeQuantityInGrams).append(" gr").append("\n");
		b.append("\tavailable milk: ").append(milkQuantityInMl).append(" ml").append("\n");
		return b.toString();
	}

	// Entry-point in your app
	public static void main(String[] args) throws Exception {
		CoffeeMaker stevie = new CoffeeMaker();
		stevie.addCoffee(200);
		stevie.addMilk(90);
		stevie.addWater(500);
		System.out.println(stevie.toString());
		
		Thread.sleep(5);
		System.out.println("We just made a: " + stevie.makeMilkFoam(2) + "\n");
		System.out.println(stevie.toString());
		
		CoffeeBrew brewed = stevie.brew("cappuccino");
		System.out.println("We just made a: " + brewed.toString() + "\n");

		brewed = stevie.brew("espresso");
		System.out.println("We just made a: " + brewed.toString() + "\n");

		System.out.println(stevie.toString());
	}
}


class CoffeeBrew {}

class Espresso extends CoffeeBrew {
	@Override
	public String toString() {
		return "espresso";
	}
}

class Cappuccino extends CoffeeBrew {
	@Override
	public String toString() {
		return "cappuccino";
	}
}

class MilkFoam {
	@Override
	public String toString() {
		return "milk foam";
	}
}

class NotEnoughWaterException extends RuntimeException {}
class NotEnoughCoffeeException extends RuntimeException {}
class NotEnoughMilkException extends RuntimeException {
	NotEnoughMilkException() {
		super("Looks like the machine doesn't have enough milk for your operation");
	}
}