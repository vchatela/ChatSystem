package services.model;

/**
 * Class FileMsg
 * Used to store information about a FileMsg
 * Is a Msg
 */
public class FileMessage extends Messages{
    public enum TransferType {ToRemoteUser, FromRemoteUser}

    private TransferType transferType;
    private int hashcodeTCP;

    public FileMessage(User sender, int hashcodeTCP, TransferType transferType) {
        super(sender);
        this.hashcodeTCP = hashcodeTCP;
        this.transferType = transferType;
    }
    public int getHashcodeTCP() {
        return hashcodeTCP;
    }
    public TransferType getTransferType() {
        return transferType;
    }
}