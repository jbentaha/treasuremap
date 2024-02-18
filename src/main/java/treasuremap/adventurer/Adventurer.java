package treasuremap.adventurer;

import lombok.Getter;
import lombok.Setter;
import treasuremap.enums.DirectionEnum;
import treasuremap.enums.InstructionEnum;
import treasuremap.enums.OrientationEnum;
import treasuremap.game.Map;

import java.util.List;

public class Adventurer implements MovableI, Comparable<Adventurer> {

    private final Map map;

    @Getter
    private final List<InstructionEnum> instructions;

    private OrientationEnum orientation;

    private final int priority;

    @Getter
    private String name;

    @Getter
    @Setter
    private int currentX;

    @Getter
    @Setter
    private int currentY;

    @Getter
    private int nbTreasures = 0;

    public Adventurer(Map map, String name, int priority, int x, int y, OrientationEnum orientation, List<InstructionEnum> instructions) {
        this.map = map;
        this.name = name;
        this.priority = priority;
        this.currentX = x;
        this.currentY = y;
        this.instructions = instructions;
        this.orientation = orientation;
    }

    public void incrementTreasures() {
        ++nbTreasures;
    }

    public void setPositions(int newX, int newY) {
        currentX = newX;
        currentY = newY;
    }

    @Override
    public void move() {
        for (InstructionEnum instruction : instructions) {

            switch (instruction) {
                case A -> map.moveAdventurer(this, getDirection());
                case D -> updateOrientation(true);
                case G -> updateOrientation(false);
            }
        }
    }

    @Override
    public int compareTo(Adventurer other) {
        return Integer.compare(priority, other.priority);
    }

    private void updateOrientation(boolean right) {
        switch (orientation) {
            case S -> orientation = right ? OrientationEnum.O : OrientationEnum.E;
            case N -> orientation = right ? OrientationEnum.E : OrientationEnum.O;
            case E -> orientation = right ? OrientationEnum.S : OrientationEnum.N;
            case O -> orientation = right ? OrientationEnum.N : OrientationEnum.S;
        }
    }

    private DirectionEnum getDirection() {
        switch (orientation) {
            case N -> {
                return DirectionEnum.UP;
            }
            case S -> {
                return DirectionEnum.DOWN;
            }
            case E -> {
                return DirectionEnum.RIGHT;
            }
            case O -> {
                return DirectionEnum.LEFT;
            }
            default -> {
                return null;
            }
        }
    }

}
