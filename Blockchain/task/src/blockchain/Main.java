package blockchain;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Add block data to queue.
        for (long blockId = 1; blockId <= 5; blockId++) {
            var blockData = new BlockData(blockId);
            BlockChain.addBlockData(blockData);
        }

        // Start miners.
        Miners.setNumberOfMiners(5);
        Miners.startMiners();

        // Wait until all block data is mined.
        while ((BlockChain.getBlockDataQueueSize() > 0) || (BlockChain.getBlockDataToMine() != null)) {
            // Sleep for a while.
            try {
                TimeUnit.MILLISECONDS.sleep(200L);
            } catch (InterruptedException ignored) {
            }
        }

        // Stop miners.
        Miners.stopMiners();

        // Get validation errors for blockchain.
        String validationErrors = BlockChain.getValidationErrors();
        if (!validationErrors.equals("")) {
            System.out.println(validationErrors);
        }

        scanner.close();
    }
}
