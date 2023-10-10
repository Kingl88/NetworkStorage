package message;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileContent {
    private byte[] content;
    private long startPosition;
    private boolean last;
    private double fileLength;
}
