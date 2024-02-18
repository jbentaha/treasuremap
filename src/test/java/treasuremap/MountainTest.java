package treasuremap;

import org.junit.jupiter.api.Test;
import treasuremap.adventurer.Adventurer;
import treasuremap.content.Element;
import treasuremap.content.Plain;
import treasuremap.content.obstacle.Mountain;
import treasuremap.game.TreasureRunner;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MountainTest extends TreasureRunner {

    private static final String INPUT_FILE_PATH = "src/test/resources/treasuremap-test-mountain.txt";

    /*
    ensure the adventurer does not climb a mountain
     */
    @Test
    public void testMountain() throws IOException {
        run();

        Element element = map.getElements()[0][1];

        assertNotNull(element, "Should be a mountain");
        assertTrue(element instanceof Mountain, "Should be a mountain");

        Element plain = map.getElements()[0][0];

        assertNotNull(plain,"Should be a plain");
        assertTrue(plain instanceof Plain,"Should be a plain");

        assertNotNull(plain.getMovable(), "Should have an adventurer");
        assertTrue(plain.getMovable() instanceof Adventurer, "Should be an adventurer");
    }

    @Override
    protected String getInputFilePath() {
        return INPUT_FILE_PATH;
    }

    @Override
    protected void printResult() {
        // do not print the result
    }
}
