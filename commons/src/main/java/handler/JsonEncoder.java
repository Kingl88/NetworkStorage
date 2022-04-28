package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import message.Message;

import java.util.List;

public class JsonEncoder extends MessageToMessageEncoder<Message> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> out) throws Exception {
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(message);
        out.add(ctx.alloc().buffer().writeBytes(bytes));
    }
}
