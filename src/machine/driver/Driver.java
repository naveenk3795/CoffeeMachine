package machine.driver;

import machine.CoffeeMachine;
import machine.data.Beverage;
import machine.data.Ingredient;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;


public class Driver {
    private static final String Machine = "machine";
    private static final String Outlets = "outlets";
    private static final String OutletsCount = "count_n";
    private static final String TotalItemsQuantity = "total_items_quantity";
    private static final String Beverages = "beverages";


    public static void main(String[] args) {
        String recipeStr = jsonString();
        JSONObject machineObj = new JSONObject(recipeStr).getJSONObject(Machine);

        int noOfOutlets = getNoOfOutlets(machineObj);
        Map<Ingredient, Integer> availableIngredients = buildIngredients(machineObj);
        Set<Beverage> beverages = buildBeveragesRecipe(machineObj, availableIngredients.keySet());

        long timeToMakeABeverage = 5000L; // Assuming time to make a beverage as 5 seconds.

        CoffeeMachine.Builder builder = new CoffeeMachine.Builder(noOfOutlets, timeToMakeABeverage);
        availableIngredients.forEach(builder::fillIngredient);
        beverages.forEach(builder::addBeverageRecipe);
        builder.build().start();
    }

    private static int getNoOfOutlets(JSONObject machineObj) {
        JSONObject countObj = machineObj.getJSONObject(Outlets);
        return countObj.getInt(OutletsCount);
    }

    private static Map<Ingredient, Integer> buildIngredients(JSONObject machineObj) {
        JSONObject ingredientsObj = machineObj.getJSONObject(TotalItemsQuantity);
        Map<Ingredient, Integer> ingredients = new HashMap<>(getIngredientsFromBeverageList(machineObj));

        Iterator<String> ite = ingredientsObj.keys();
        while (ite.hasNext()) {
            String name = ite.next();
            Integer quantity = ingredientsObj.getInt(name);
            ingredients.put(new Ingredient(name), quantity);
        }

        return ingredients;
    }

    // Doing this, because some ingredients in the beverage recipe list are not in initial available quantity
    private static Map<Ingredient, Integer> getIngredientsFromBeverageList(JSONObject machineObj) {
        JSONObject beveragesObj = machineObj.getJSONObject(Beverages);
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        Iterator<String> beverageNameItr = beveragesObj.keys();
        while (beverageNameItr.hasNext()) {
            String beverageName = beverageNameItr.next();
            JSONObject recipeObj = beveragesObj.getJSONObject(beverageName);
            Iterator<String> ingredientsNameItr = recipeObj.keys();
            while(ingredientsNameItr.hasNext()) {
                String ingredientName = ingredientsNameItr.next();
                Ingredient ingredient = new Ingredient(ingredientName);
                ingredients.put(ingredient, 0);
            }
        }
        return ingredients;
    }

    private static Set<Beverage> buildBeveragesRecipe(JSONObject machineObj, Set<Ingredient> ingredients) {
        JSONObject beveragesObj = machineObj.getJSONObject(Beverages);
        Map<String, Ingredient> nameIngredientMap = ingredients.stream().collect(Collectors.toMap(Ingredient::getName, i -> i));
        Set<Beverage> beverages = new HashSet<>();
        Iterator<String> beverageNameItr = beveragesObj.keys();
        while (beverageNameItr.hasNext()) {
            String beverageName = beverageNameItr.next();
            JSONObject recipeObj = beveragesObj.getJSONObject(beverageName);
            beverages.add(buildBeverageRecipe(recipeObj, beverageName, nameIngredientMap));
        }
        return beverages;
    }

    private static Beverage buildBeverageRecipe(JSONObject recipeObj, String beverageName, Map<String, Ingredient> nameIngredientMap) {
        Beverage.Builder builder = new Beverage.Builder(beverageName);
        Iterator<String> ingredientsNameItr = recipeObj.keys();
        while(ingredientsNameItr.hasNext()) {
            String ingredientName = ingredientsNameItr.next();
            int quantity = recipeObj.getInt(ingredientName);
            builder = builder.addToRecipe(nameIngredientMap.get(ingredientName), quantity);
        }
        return builder.build();
    }

    private static String jsonString() {
        return "{\n" +
                "  \"machine\": {\n" +
                "    \"outlets\": {\n" +
                "      \"count_n\": 4\n" +
                "    },\n" +
                "    \"total_items_quantity\": {\n" +
                "      \"hot_water\": 500,\n" +
                "      \"hot_milk\": 500,\n" +
                "      \"ginger_syrup\": 100,\n" +
                "      \"sugar_syrup\": 100,\n" +
                "      \"tea_leaves_syrup\": 100\n" +
                "    },\n" +
                "    \"beverages\": {\n" +
                "      \"hot_tea\": {\n" +
                "        \"hot_water\": 200,\n" +
                "        \"hot_milk\": 100,\n" +
                "        \"ginger_syrup\": 10,\n" +
                "        \"sugar_syrup\": 10,\n" +
                "        \"tea_leaves_syrup\": 30\n" +
                "      },\n" +
                "      \"hot_coffee\": {\n" +
                "        \"hot_water\": 100,\n" +
                "        \"ginger_syrup\": 30,\n" +
                "        \"hot_milk\": 400,\n" +
                "        \"sugar_syrup\": 50,\n" +
                "        \"tea_leaves_syrup\": 30\n" +
                "      },\n" +
                "      \"black_tea\": {\n" +
                "        \"hot_water\": 300,\n" +
                "        \"ginger_syrup\": 30,\n" +
                "        \"sugar_syrup\": 50,\n" +
                "        \"tea_leaves_syrup\": 30\n" +
                "      },\n" +
                "      \"green_tea\": {\n" +
                "        \"hot_water\": 100,\n" +
                "        \"ginger_syrup\": 30,\n" +
                "        \"sugar_syrup\": 50,\n" +
                "        \"green_mixture\": 30\n" +
                "      },\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }
}
