package machine;

import machine.data.Beverage;
import machine.data.Ingredient;
import machine.worker.Worker;
import machine.worker.WorkerHandler;

import java.util.*;
import java.util.stream.Collectors;

public class CoffeeMachine {
    int noOfOutlets;
    final Map<Ingredient, Integer> availableIngredients;
    Map<Integer, Beverage> beverages;
    WorkerHandler handler;
    Scanner scan;
    private final int maxQuantityOfEachIngredient = 1000; // Assuming max quantity of each ingredient that can be stored

    private CoffeeMachine(int noOfOutlets, long timeToMakeBeverage) {
        if(noOfOutlets <= 0) {
            throw new IllegalArgumentException("no of outlets has to be at least one");
        }
        this.noOfOutlets = noOfOutlets;
        availableIngredients = new HashMap<>();
        beverages = new HashMap<>();
        handler = new WorkerHandler(noOfOutlets, timeToMakeBeverage);
        scan = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Starting coffee machine");
        while (true) {
            System.out.print("Press 0 to refill an ingredient, # to display ingredients in stock, -1 to Quit or ");
            System.out.println("Press the number associated with the drink you want");
            printAvailableBeverages();
            String userInput = scan.next();

            if ("-1".equals(userInput)) {
                break;
            }
            if("0".equals(userInput)) {
                refillIngredient();
                continue;
            }
            if("#".equals(userInput)) {
                displayIngredientsStockInfo();
                continue;
            }
            Integer userInputInt = convertToNumber(userInput);
            if (!beverages.containsKey(userInputInt)) {
                System.out.println("Invalid option. Please try again.");
                continue;
            }
            Beverage toMake = beverages.get(userInputInt);
            makeBeverage(toMake);
        }
        handler.shutdown();
    }

    private void displayIngredientsStockInfo() {
        System.out.println("Ingredients and stock level");
        availableIngredients.forEach(((ingredient, quantity) -> System.out.println(ingredient + " - " + quantity)));
    }

    private void refillIngredient() {
        Map<Integer, Ingredient> indexIngredientMap = getIndexIngredientMap();
        while(true) {
            printIngredientInfo(indexIngredientMap);
            int userInput = scan.nextInt();
            Ingredient ingredientToRefill = indexIngredientMap.get(userInput);
            if(ingredientToRefill == null) {
                System.out.println("Invalid option. Please try again.");
                continue;
            }
            availableIngredients.put(ingredientToRefill, maxQuantityOfEachIngredient);
            System.out.println(ingredientToRefill + " is filled to maximum quantity");
            break;
        }
    }

    private void makeBeverage(Beverage toMake) {
        synchronized (availableIngredients) {
            List<Ingredient> inAdequateIngredients = areAllIngredientsAvailable(toMake);
            if (!inAdequateIngredients.isEmpty()) {
                System.out.println(getCannotBePreparedString(toMake, inAdequateIngredients));
                return;
            }
            Worker worker = handler.getFreeOutlet(toMake);
            if (worker == null) {
                System.out.println("No outlet is free, please try again later");
                return;
            }
            subtractRequiredIngredients(toMake);
            handler.startWorker(worker);
        }
    }

    private List<Ingredient> areAllIngredientsAvailable(Beverage toMake) {
        List<Ingredient> inAdequateIngredients = new ArrayList<>();
        for (Map.Entry<Ingredient, Integer> entry : toMake.getRequiredIngredients().entrySet()) {
            Ingredient ingredient = entry.getKey();
            int requiredQuantity = entry.getValue();

            if (this.availableIngredients.getOrDefault(ingredient, 0) < requiredQuantity) {
                inAdequateIngredients.add(ingredient);
            }
        }
        return inAdequateIngredients;
    }

    private void subtractRequiredIngredients(Beverage toMake) {
        for (Map.Entry<Ingredient, Integer> entry : toMake.getRequiredIngredients().entrySet()) {
            Ingredient ingredient = entry.getKey();
            int requiredQuantity = entry.getValue();

            this.availableIngredients.computeIfPresent(ingredient, (i, existing) -> existing - requiredQuantity);
        }
    }

    private void printAvailableBeverages() {
        for (Map.Entry<Integer, Beverage> entry : beverages.entrySet()) {
            System.out.print(entry.getKey() + ": " + entry.getValue() + " ");
        }
        System.out.println();
    }


    private Integer convertToNumber(String userInput) {
        try {
            return Integer.parseInt(userInput);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Map<Integer, Ingredient> getIndexIngredientMap() {
        Map<Integer, Ingredient> indexIngredientMap = new HashMap<>();
        Set<Ingredient> ingredients = availableIngredients.keySet();
        int index = 1;
        for(Ingredient ingredient: ingredients) {
            indexIngredientMap.put(index++, ingredient);
        }
        return indexIngredientMap;
    }

    private void printIngredientInfo(Map<Integer, Ingredient> indexIngredientMap) {
        System.out.println("Press the number associated with the ingredient to fill");
        for (Map.Entry<Integer, Ingredient> entry : indexIngredientMap.entrySet()) {
            System.out.print(entry.getKey() + ": " + entry.getValue() + " ");
        }
        System.out.println();
    }

    private String getCannotBePreparedString(Beverage toMake, List<Ingredient> inAdequateIngredients) {
        return toMake.toString() + " cannot be prepared because " +
                inAdequateIngredients.stream().map(Ingredient::toString).collect(Collectors.joining(",")) +
                (inAdequateIngredients.size() == 1 ? " is" : " are") +
                " not available.";
    }

    public static class Builder {
        private final CoffeeMachine instance;
        private final Set<Beverage> beverages;

        public Builder(int noOfOutlets, long timeToMakeBeverage) {
            this.instance = new CoffeeMachine(noOfOutlets, timeToMakeBeverage);
            this.beverages = new HashSet<>();
        }

        public Builder fillIngredient(Ingredient ingredient, int quantity) {
            if(ingredient == null) {
                throw new IllegalArgumentException("Invalid ingredient");
            }
            this.instance.availableIngredients.put(ingredient, quantity);
            return this;
        }

        public Builder addBeverageRecipe(Beverage beverage) {
            this.beverages.add(beverage);
            return this;
        }

        public CoffeeMachine build() {
            int index = 1;
            for (Beverage beverage : beverages) {
                instance.beverages.put(index++, beverage);
            }
            return this.instance;
        }
    }
}
