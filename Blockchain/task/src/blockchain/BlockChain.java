package blockchain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockChain {
    private static List<String> messages;
    private static final LinkedHashSet<Block> blockChain = new LinkedHashSet<>();
    private static final int DESIRED_SOLUTION_SECONDS = 2;
    private static final int DESIRED_SOLUTION_PLUS_MINUS = 1;

    private static final AtomicInteger targetNumberOfZeros = new AtomicInteger();
    private static final ArrayDeque<BlockData> blockDataQueue = new ArrayDeque<>();
    private static String previousHash = "0";
    private static final AtomicReference<BlockData> blockDataToMine = new AtomicReference<>();

    static {
        targetNumberOfZeros.set(0);
        blockDataToMine.set(null);
    }

    public BlockChain() {
    }

    public static int getTargetNumberOfZeros() {
        return targetNumberOfZeros.get();
    }

    public static void setTargetNumberOfZeros(int value) {
        targetNumberOfZeros.set(value);
    }

    // Add block data to queue.
    public synchronized static void addBlockData(BlockData blockData) {
        // Add block data to queue.
        blockDataQueue.add(blockData);

        // Is there no block data for miners to work on?
        if (blockDataToMine.get() == null) {
            giveBlockDataToMiners();
        }
    }

    // Give next block data queue item to miners.
    private synchronized static void giveBlockDataToMiners() {
        // Get block data from queue.
        var blockData = blockDataQueue.remove();

        // Set previous hash.
        blockData.setPreviousHash(previousHash);

        // Make block data available to miners.
        blockDataToMine.set(blockData);
    }

    public static int getBlockDataQueueSize() {
        return blockDataQueue.size();
    }

    public static BlockData getBlockDataToMine() {
        return blockDataToMine.get();
    }

    // Submit finished block for adding to blockchain.
    public synchronized static boolean submitFinishedBlock(Block block, int minerId, long durationSeconds) {
        // Was block already added?
        if (blockDataToMine.get() == null) {
            return false;
        }

        // Is block invalid?
        if (isBlockInvalid(block, previousHash, true)) {
            return false;
        }
        block.getBlockData().setMessages(new ArrayList<>(messages));

        // Add block to blockchain.
        blockChain.add(block);
        previousHash = block.getHash();

        // Print block information.
        printInformation(block, minerId, durationSeconds);

        // Adjust proof of work complexity based on time for solution.
        if (durationSeconds < (DESIRED_SOLUTION_SECONDS - DESIRED_SOLUTION_PLUS_MINUS)) {
            targetNumberOfZeros.incrementAndGet();
            System.out.printf("N was increased to %d\n\n", targetNumberOfZeros.get());
        } else if (durationSeconds > (DESIRED_SOLUTION_SECONDS + DESIRED_SOLUTION_PLUS_MINUS)) {
            targetNumberOfZeros.decrementAndGet();
            System.out.print("N was decreased by 1\n\n");
        } else {
            System.out.print("N stays the same\n\n");
        }

        // Is there more block data in queue?
        if (!blockDataQueue.isEmpty()) {
            giveBlockDataToMiners();
        } else {
            blockDataToMine.set(null);
        }
        messages.clear();
        return true;
    }

    // Is block invalid?
    private static boolean isBlockInvalid(Block block, String previousHash, boolean checkProofOfWork) {
        final Pattern leadingZeroPattern = Pattern.compile("^(0+)");

        // Does previous block hash not match?
        if (!block.getBlockData().getPreviousHash().equals(previousHash)) {
            return true;
        }

        // Does block data hash not match block hash?
        String hash = hashBlockData(block.getBlockData());
        if (!hash.equals(block.getHash())) {
            return true;
        }

        // Do we not need to check proof of work?
        if (!checkProofOfWork) {
            return false;
        }

        // Was proof of work not satisfied?
        Matcher leadingZeroMatcher = leadingZeroPattern.matcher(hash);
        if (leadingZeroMatcher.find()) {
            int leadingZeroCount = leadingZeroMatcher.group(1).length();
            return leadingZeroCount < targetNumberOfZeros.get();
        } else {
            return true;
        }
    }

    // Print block information.
    private static void printInformation(Block block, int minerId, long durationSeconds) {
        var blockData = block.getBlockData();
        String hash = block.getHash();
        System.out.println("Block:");
        System.out.printf("Created by miner # %d\n", minerId);
        System.out.printf("Id: %d\n", blockData.getBlockId());
        System.out.printf("Timestamp: %d\n", blockData.getTimeStamp());
        System.out.printf("Magic number: %d\n", blockData.getNonce());
        System.out.println("Hash of the previous block:");
        System.out.println(blockData.getPreviousHash());
        System.out.println("Hash of the block:");
        System.out.println(hash);
        System.out.printf("Block was generating for %d seconds\n", durationSeconds);
    }

    // Hash block data.
    public static String hashBlockData(BlockData blockData) {
        String blockDataString = Utils.serializeObject(blockData);
        return Utils.hashString(blockDataString);
    }

    // Get validation errors for blockchain.
    public synchronized static String getValidationErrors() {
        var results = new StringBuilder();

        // Loop through blockchain.
        String previousHash = "0";
        for (Block block : blockChain) {
            // Is block invalid?
            if (isBlockInvalid(block, previousHash, false)) {
                results.append(String.format("%s is invalid.\n", getBlockIdentifier(block)));
            }
            previousHash = block.getHash();
        }

        return results.toString();
    }

    public synchronized static void sendMessage(String message) {
        messages.add(message);
    }

    private static String getBlockIdentifier(Block block) {
        return String.format("Block with id %s", block.getBlockData().getBlockId());
    }
}