package blockchain;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Miners {
    private static int numberOfMiners;
    private static ExecutorService executor;

    public Miners() {
    }

    public static int getNumberOfMiners() {
        return numberOfMiners;
    }

    public static void setNumberOfMiners(int numberOfMiners) {
        Miners.numberOfMiners = numberOfMiners;
    }

    // Start miners.
    public static void startMiners() {
        executor = Executors.newFixedThreadPool(numberOfMiners);
        for (int i = 1; i <= numberOfMiners; i++) {
            executor.execute(new MinerThread(i));
        }
    }

    // Stop miners.
    public static void stopMiners() {
        executor.shutdown();
        while (!executor.isTerminated()) {
            // Sleep for a while.
            try {
                TimeUnit.MILLISECONDS.sleep(200L);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static class MinerThread implements Runnable {
        private final Pattern leadingZeroPattern = Pattern.compile("^(0+)");
        private final int minerId;

        public MinerThread(int minerId) {
            this.minerId = minerId;
        }

        @Override
        public void run() {
            long currentBlockId = -1;
            Instant start = Instant.now();

            // Loop until shutdown.
            while (!executor.isShutdown()) {
                // Is there block data to mine?
                var blockData = BlockChain.getBlockDataToMine();
                if (blockData != null) {
                    // Are we on a new block?
                    if (blockData.getBlockId() != currentBlockId) {
                        // Save start time.
                        start = Instant.now();
                        currentBlockId = blockData.getBlockId();
                    }
                    // Try a random nonce to satisfy proof of work.
                    tryANonce(blockData, start);
                }
            }
        }

        // Try a random nonce to satisfy proof of work.
        private void tryANonce(BlockData blockData, Instant start) {
            // Get target number of zeros.
            int targetNumberOfZeros = BlockChain.getTargetNumberOfZeros();

            // Make clone of block data to manipulate.
            var blockDataLocal = (BlockData) blockData.clone();

            // Set timestamp.
            blockDataLocal.setTimeStamp(new Date().getTime());

            // Set nonce.
            blockDataLocal.setNonce((long) (Math.random() * 1_000_000_000L));

            // Hash block data.
            String hash = BlockChain.hashBlockData(blockDataLocal);

            // Did we satisfy proof of work?
            Matcher leadingZeroMatcher = leadingZeroPattern.matcher(hash);
            if (leadingZeroMatcher.find()) {
                int leadingZeroCount = leadingZeroMatcher.group(1).length();
                if (leadingZeroCount >= targetNumberOfZeros) {
                    // Calculate time taken to solve.
                    Instant end = Instant.now();
                    long durationSeconds = Duration.between(start, end).getSeconds();
                    // Submit finished block for adding to blockchain.
                    BlockChain.submitFinishedBlock(new Block(blockDataLocal, hash), minerId, durationSeconds);
                }
            }
        }
    }
}