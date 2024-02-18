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
        if(isBlocked(futureBox)) { // either the element is an obstacle or an adventurer is already in place
            return;
        }

        futureBox.addToQueue(adventurer);

        actuallyMove(newX, newY, currentX, currentY, futureBox, adventurer);
    }

    private void actuallyMove(int newX, int newY, int currentX, int currentY, Element futureBox, Adventurer adventurer) {

        Adventurer priorityAdventurer = futureBox.getPriorityAdventurer();

        // check if currentThread(Adventurer) is the same(same reference) as the priorityAdventurer
        if(adventurer == priorityAdventurer) {

            Element newElement = elements[newY][newX];
            Element currentElement = elements[currentY][currentX];

            try {
                // Acquire locks on both current and next boxes to move safely
                currentElement.acquireLock();
                newElement.acquireLock();

                priorityAdventurer.setPositions(newX, newY);

                currentElement.setMovable(null);
                newElement.setMovable(priorityAdventurer);

                if(decrementIfHasTreasures(newElement)) {
                    priorityAdventurer.incrementTreasures();
                }

                newElement.clearQueue();

            } finally {
                currentElement.releaseLock();
                newElement.releaseLock();
            }
        }
    }

    private boolean isBlocked(Element futureBox) {
        return futureBox instanceof ObstacleI || futureBox.getMovable() != null;
    }

    private boolean decrementIfHasTreasures(Element newElement) {
        return newElement instanceof Treasure treasure && treasure.decreaseNbTreasures();
    }

}
