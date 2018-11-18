package fr.polytech.tasks;

public class TriggerProcess implements Runnable {

    private QueueTask task;

    public TriggerProcess(QueueTask task){
        this.task = task;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                task.leaseTasks();
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
