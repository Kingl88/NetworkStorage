package message;

public class FileContent {
    private byte[] content;
    private long startPosition;
    private boolean last;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(final long startPosition) {
        this.startPosition = startPosition;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(final boolean last) {
        this.last = last;
    }

}
