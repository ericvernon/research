package main;

public class Watch {
    private long startTime;
    private long endTime;
    private String name;

    public Watch(String name) {
        this.name = name;
    }

    public void start() {
        this.startTime = System.nanoTime();
    }

    public void disp(String name) {
        long now = System.nanoTime();
        System.out.println(this.name + ":" + name + ": " + ((now - startTime) * 1e-6) + " milliseconds");
    }

    public void done() {
        System.out.println("-----" + this.name + "-----");
    }
}
