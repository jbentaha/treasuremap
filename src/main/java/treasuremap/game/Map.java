package treasuremap.game;

import lombok.Getter;
import treasuremap.adventurer.Adventurer;
import treasuremap.content.Element;
import treasuremap.content.Plain;
import treasuremap.content.Treasure;
import treasuremap.enums.DirectionEnum;
import treasuremap.content.obstacle.ObstacleI;

public class Map {

    @Getter
    private Element[][] elements;

    public Map(int rows, int columns) {
        elements = new Element[rows][columns];
    }

    public void addElement(Element box, int x, int y) {
        elements[y][x] = box;
    }

    // this method should be called last when populating the map
    public void fillPlainElements() {
        for (int i = 0; i < elements.length; i++) {
            for (int j = 0; j < elements[i].length; j++) {
                if(elements[i][j] == null) {
                    elements[i][j] = new Plain();
                }
            }
        }
    }

    public void moveAdventurer(Adventurer adventurer, DirectionEnum direction) {

        int currentX = adventurer.getCurrentX();
        int currentY = adventurer.getCurrentY();
        int newX = currentX;
        int newY = currentY;

        switch (direction) {
            case UP -> {
                newY = Math.max(0, currentY - 1);
            }
            case DOWN -> {
                newY = Math.min(elements.length - 1, currentY + 1);
            }
            case LEFT -> {
                newX = Math.max(0, currentX - 1);
            }
            case RIGHT -> {
                newX = Math.min(elements[0].length - 1, currentY + 1);
            }
        }

        Element futureBox = elements[newY][newX];
        if(futureBox instanceof ObstacleI || futureBox.getMovable() != null) { // either the element is an obstacle or an adventurer is already in place
            return;
        }

        futureBox.addToQueue(adventurer);

        // a small delay before occupying the new box : allows adventures to subscribe.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        actuallyMove(newX, newY, currentX, currentY, futureBox);
    }

    private void actuallyMove(int newX, int newY, int currentX, int currentY, Element futureBox) {

        Adventurer priorityAdventurer = futureBox.getPriorityQueue().poll();

        try {
            // Acquire locks on both current and next boxes to move safely
            elements[currentY][currentX].getLock().lock();
            elements[newY][newX].getLock().lock();

            priorityAdventurer.setCurrentX(newX);
            priorityAdventurer.setCurrentY(newY);

            elements[currentY][currentX].setMovable(null);
            elements[newY][newX].setMovable(priorityAdventurer);

            if(elements[newY][newX] instanceof Treasure && ((Treasure) elements[newY][newX]).decreaseNbTreasures()) {
                priorityAdventurer.incrementTreasures();
            }

            elements[newY][newX].clearQueue();

        } finally {
            elements[currentY][currentX].getLock().unlock();
            elements[newY][newX].getLock().unlock();
        }
    }

}
