package treasuremap;

import org.junit.jupiter.api.Test;
import treasuremap.content.Plain;
import treasuremap.game.TreasureRunner;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MapBoundariesTest extends TreasureRunner {

    private static final String INPUT_FILE_PATH = "src/test/resources/treasuremap-test-boundaries.txt";

    /*
    ensure the adventurer did not move at all
     */
    @Test
    public void testBoundaries() throws IOException {
        run();

        Plain element = (Plain) map.getElements()[0][0];

        assertNotNull(element.getMovable(),"Should have the adventurer");
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
