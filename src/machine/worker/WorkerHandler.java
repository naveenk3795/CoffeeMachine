package machine.worker;

import machine.data.Beverage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerHandler {
    private final ExecutorService executor;
    private final List<Worker> freeWorkers;

    public WorkerHandler(int totalOutlets, long timeToMakeABeverage) {
        executor = Executors.newFixedThreadPool(totalOutlets);
        freeWorkers = new ArrayList<>(totalOutlets);
        for (int i = 1; i <= totalOutlets; i++) {
            freeWorkers.add(new Worker(i, this, timeToMakeABeverage));
        }
    }

    public Worker getFreeOutlet(Beverage toMake) {
        synchronized (this) {
            if (freeWorkers.isEmpty()) {
                return null;
            }
            return freeWorkers.remove(0).setBeverageToMake(toMake);
        }
    }

    public void startWorker(Worker worker) {
        executor.submit(worker);
    }

    public void shutdown() {
        executor.shutdown();
    }

    synchronized void callback(Worker worker) {
        freeWorkers.add(worker);
    }
}
