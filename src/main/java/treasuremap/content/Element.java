package treasuremap.content;

import lombok.Getter;
import lombok.Setter;
import treasuremap.adventurer.Adventurer;
import treasuremap.adventurer.MovableI;
import treasuremap.content.obstacle.ObstacleI;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Element {

    private static final ThreadLocal<StringBuilder> threadLocal = new ThreadLocal<StringBuilder>() {

        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }

        @Override
        public StringBuilder get() {
            StringBuilder stringBuilder = super.get();
            stringBuilder.setLength(0);
            return stringBuilder;
        }

    };

    @Getter
    private PriorityBlockingQueue<Adventurer> priorityQueue;

    @Getter
    private Lock lock;

    @Getter
    @Setter
    private MovableI movable;

    protected StringBuilder getStringBuilder() {
        return threadLocal.get();
    }

    public Element() {
        if(!(this instanceof ObstacleI)) {
            priorityQueue = new PriorityBlockingQueue<>();
            lock = new ReentrantLock();
        }
    }

    public void addToQueue(Adventurer adventurer) {
        priorityQueue.add(adventurer);
    }

    public PriorityBlockingQueue<Adventurer> getPriorityQueue() {
        return priorityQueue;
    }

    public void clearQueue() {
        priorityQueue.clear();
    }

    public Lock getLock() {
        return lock;
    }

    public void acquireLock() {
        lock.lock();
    }

    public void releaseLock() {
        lock.unlock();
    }

    public Adventurer getPriorityAdventurer() {
        return priorityQueue.poll();
    }
}
