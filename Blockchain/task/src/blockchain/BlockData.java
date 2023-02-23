package blockchain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BlockData implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String previousHash; // Set by blockchain.
    private long timeStamp; // Set by miners.
    private long nonce; // Set by miners.

    // Block data fields. Set by users.
    private final long blockId;

    private List<String> messages;

    public BlockData(long blockId) {
        this.blockId = blockId;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public long getBlockId() {
        return blockId;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Unexpected error cloning block data.");
        }
    }
}