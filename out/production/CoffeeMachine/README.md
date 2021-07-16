Coffee machine

The code is designed in such a way that, any ingredient or beverage can be configured. 
No of outlets is handled by a thread pool of the said size.

The machine class has the following attributes
1. Available Ingredients - A Map of Ingredient-Quantity in stock
2. Beverage recipes - Stores information about what ingredients needed to make a beverage
3. No of outlets
4. Start function - takes user input and makes beverage accordingly
5. Make beverage function - has three components
    1. Checks if all required ingredients are of adequate quantity - If not enough ingredients found, it stops the process
    2. Gets an available outlet - if no outlet is free, it stops the process
    3. Subtract the required ingredients from store and starts worker thread.
    
Key Assumptions:
1. Assuming that each beverage takes 5 seconds to be mode - But this can be easily configured in Driver class
2. Maximum quantity of an ingredient that can be stored in the machine is assumed to be 1000ml per ingredient. This is hard coded in machine class, since I'm not sure what is expected here.
3. Are all ingredients adequate check is done before checking for free outlet, but this can be altered.

Driver class:
    Driver class takes a configuration json that will have recipes to make beverages and initial ingredients, instantiates machine and starts it
    Hack -> getIngredientsFromBeverageList function is necessary since some required ingredients for some beverages may not be available in initial list of ingredients in the input.

All beverage, ingredient, worker objects are instantiated once per entity. They are reused across the app.

Design patterns used:
1. Builder pattern is used to build CoffeeMachine, Beverage objects
2. CoffeeMachine is a singleton
3. Workers is an object pool design

External libraries used:
1. org.json - link: https://search.maven.org/remotecontent?filepath=org/json/json/20210307/json-20210307.jar