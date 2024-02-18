package treasuremap.game;

import lombok.extern.slf4j.Slf4j;
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

    private List<ElementPositions> elementPositions = new ArrayList<>();

    private Queue<MovableI> adventurers = new PriorityQueue<>();

    private Map map;

    public void run() throws IOException {
        initGameMap();
        runGame();
    }

    private void runGame() throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(adventurers.size());

        while(!adventurers.isEmpty()) {
            MovableI adventurer = adventurers.poll();

            executorService.submit(() -> {
                adventurer.move();
            });
        }

        executorService.shutdown();

        /*try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("An adventurer was interrupted");
            throw new RuntimeException("An adventurer was interrupted");
        }*/

        printResult();
    }

    private void printResult() throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {

            Element[][] elements = map.getElements();
            for (int i = 0; i < elements.length; i++) {
                for (int j = 0; j < elements[i].length; j++) {
                    Element element = elements[i][j];
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
        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE_PATH))) {

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

    private void processLine(String line, int counter) {
        String[] split = line.split(TRMStringUtils.DASH);

        ElementPositions elementPosition = null;

        ElementEnum elementEnum = getElement(split[0].trim());
        switch (elementEnum) {
            case A -> {
                elementPosition = createAdventurer(split, counter);
            }
            case C -> {
                map = createMap(split);
            }
            case M -> {
                elementPosition = createMountain(split);
            }
            case T -> {
                elementPosition = createTreasure(split);
            }
        }

        if(elementPosition != null) {
            elementPositions.add(elementPosition);
        }
    }

    private ElementPositions createTreasure(String[] split) {
        Integer x = Integer.valueOf(split[1].trim());
        Integer y = Integer.valueOf(split[2].trim());
        Integer nbTreasures = Integer.valueOf(split[3].trim());

        Treasure treasure = new Treasure(nbTreasures);

        return new ElementPositions(treasure, x, y);
    }

    private ElementPositions createMountain(String[] split) {
        Integer x = Integer.valueOf(split[1].trim());
        Integer y = Integer.valueOf(split[2].trim());
        return new ElementPositions(new Mountain(), x, y);
    }

    private ElementPositions createAdventurer(String[] split, int priority) {
        Plain plain = new Plain();

        String name = split[1].trim();
        Integer x = Integer.valueOf(split[2].trim());
        Integer y = Integer.valueOf(split[3].trim());
        OrientationEnum orientation = getOrientation(split[4].trim());
        List<InstructionEnum> instructions = getInstructions(split[5].trim());

        Adventurer adventurer = new Adventurer(map, name, priority, x, y, orientation, instructions);
        adventurers.add(adventurer);

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
        int columns = Integer.valueOf(split[1].trim());
        int rows = Integer.valueOf(split[2].trim());

        return new Map(rows, columns);
    }

    private ElementEnum getElement(String element) {
        return ElementEnum.valueOf(element);
    }

}
