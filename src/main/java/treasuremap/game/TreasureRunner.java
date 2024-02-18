package treasuremap.game;

import treasuremap.adventurer.Adventurer;
import treasuremap.adventurer.MovableI;
import treasuremap.content.Element;
import treasuremap.content.Plain;
import treasuremap.content.Treasure;
import treasuremap.content.obstacle.Mountain;
import treasuremap.enums.ElementEnum;
import treasuremap.enums.InstructionEnum;
import treasuremap.enums.OrientationEnum;
import treasuremap.utils.TRMStringUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TreasureRunner {

    private static final int TAB = 20;

    private static final String INPUT_FILE_PATH = "src/main/resources/treasuremap-input-data.txt";

    private static final String OUTPUT_FILE_PATH = "src/main/resources/treasuremap-output-data.txt";

    private final List<ElementPositions> elementPositions = new ArrayList<>();

    private final Queue<MovableI> movables = new PriorityQueue<>();

    protected Map map;

    public void run() throws IOException {
        initGameMap();
        runGame();
    }

    private void runGame() throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(movables.size());

        while(!movables.isEmpty()) {
            MovableI movable = movables.poll();

            executorService.submit(movable::move);
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("An adventurer was interrupted");
            throw new RuntimeException("An adventurer was interrupted");
        }

        printResult();
    }

    protected void printResult() throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {

            Element[][] elements = map.getElements();
            for (Element[] value : elements) {
                for (Element element : value) {
                    String elementString = element.toString();
                    writer.write(elementString);
                    writeSpaces(writer, elementString);
                }

                writer.newLine();
            }

            writer.flush();

            System.out.println("Congratulations: check the result file");
        } catch (IOException e) {
            System.err.print("Problem occurred writing the result file");
            throw e;
        }
    }

    private void writeSpaces(BufferedWriter writer, String elementString) throws IOException {
        for (int i = 0; i < TAB - elementString.length(); i++) {
            writer.write(TRMStringUtils.SPACE);
        }
    }

    private void initGameMap() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(getInputFilePath()))) {

            String line;
            int counter = 1;
            while ((line = br.readLine()) != null) {
                if(!line.startsWith(TRMStringUtils.HASH_MARK)) {
                    processLine(line, counter);
                    counter++;
                }
            }

        } catch (IOException e) {
            System.err.println("Game initialization failure : please check your entry game data file");
            throw e;
        }

        for (ElementPositions item: elementPositions) {
            map.addElement(item.element(), item.positionX(), item.positionY());
        }
        map.fillPlainElements();
    }

    protected String getInputFilePath() {
        return INPUT_FILE_PATH;
    }

    private void processLine(String line, int counter) {
        String[] split = line.split(TRMStringUtils.DASH);

        ElementPositions elementPosition = null;

        ElementEnum elementEnum = getElement(split[0].trim());
        switch (elementEnum) {
            case A -> elementPosition = createAdventurer(split, counter);
            case C -> map = createMap(split);
            case M -> elementPosition = createMountain(split);
            case T -> elementPosition = createTreasure(split);
        }

        if(elementPosition != null) {
            elementPositions.add(elementPosition);
        }
    }

    private ElementPositions createTreasure(String[] split) {
        int x = Integer.parseInt(split[1].trim());
        int y = Integer.parseInt(split[2].trim());
        int nbTreasures = Integer.parseInt(split[3].trim());

        Treasure treasure = new Treasure(nbTreasures);

        return new ElementPositions(treasure, x, y);
    }

    private ElementPositions createMountain(String[] split) {
        int x = Integer.parseInt(split[1].trim());
        int y = Integer.parseInt(split[2].trim());
        return new ElementPositions(new Mountain(), x, y);
    }

    private ElementPositions createAdventurer(String[] split, int priority) {
        Plain plain = new Plain();

        String name = split[1].trim();
        int x = Integer.parseInt(split[2].trim());
        int y = Integer.parseInt(split[3].trim());
        OrientationEnum orientation = getOrientation(split[4].trim());
        List<InstructionEnum> instructions = getInstructions(split[5].trim());

        Adventurer adventurer = new Adventurer(map, name, priority, x, y, orientation, instructions);
        movables.add(adventurer);

        plain.setMovable(adventurer);
        return new ElementPositions(plain, x, y);
    }

    private List<InstructionEnum> getInstructions(String instructionsSuite) {
        List<InstructionEnum> instructions = new ArrayList<>();

        for (char c: instructionsSuite.toCharArray()) {
            instructions.add(InstructionEnum.valueOf(String.valueOf(c)));
        }

        return instructions;
    }

    private OrientationEnum getOrientation(String orientationChar) {
        return OrientationEnum.valueOf(orientationChar);
    }

    private Map createMap(String[] split) {
        int columns = Integer.parseInt(split[1].trim());
        int rows = Integer.parseInt(split[2].trim());

        if(rows == 0 && columns == 0) {
            throw new IllegalArgumentException("Cannot initialize map");
        }

        return new Map(rows, columns);
    }

    private ElementEnum getElement(String element) {
        return ElementEnum.valueOf(element);
    }

}
