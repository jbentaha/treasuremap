package treasuremap.content;

import lombok.Getter;
import lombok.Setter;
import treasuremap.adventurer.Adventurer;
import treasuremap.adventurer.MovableI;
import treasuremap.content.obstacle.ObstacleI;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public abstract class Element {

    private static final ThreadLocal<StringBuilder> threadLocal = new ThreadLocal<>() {

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

    private PriorityBlockingQueue<Adventurer> priorityQueue;

    private Lock lock;

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

    public void clearQueue() {
        priorityQueue.clear();
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
