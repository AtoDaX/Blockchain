package blockchain;

public class Block {
    private final BlockData blockData;
    private final String hash;

    public Block(BlockData blockData, String hash) {
        this.blockData = blockData;
        this.hash = hash;
    }

    public BlockData getBlockData() {
        return blockData;
    }

    public String getHash() {
        return hash;
    }
}