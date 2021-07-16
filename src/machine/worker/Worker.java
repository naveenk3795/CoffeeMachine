package machine.worker;

import machine.data.Beverage;

public class Worker implements Runnable {
    private int id;
    WorkerHandler handlerRef;
    Beverage beverageToMake;
    private final long timeToMakeABeverage;

    public Worker(int id, WorkerHandler handlerRef, long timeToMakeABeverage) {
        this.id = id;
        this.handlerRef = handlerRef;
        this.timeToMakeABeverage = timeToMakeABeverage;
    }

    public Worker setBeverageToMake(Beverage beverageToMake) {
        this.beverageToMake = beverageToMake;
        return this;
    }

    @Override
    public void run() {
        System.out.println("Making Beverage: " + beverageToMake);
        try {
            Thread.sleep(timeToMakeABeverage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Your " + beverageToMake +  " is ready at outlet " + id);
        handlerRef.callback(this);
    }


}
