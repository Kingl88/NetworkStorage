package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import message.Message;

import java.util.List;

public class JsonEncoder extends MessageToMessageEncoder<Message> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();//класс позволяющий преобразовывать из JSON и в JSON.

    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> out) throws Exception {
        String str = OBJECT_MAPPER.writeValueAsString(message);//преобразовываем объект типа Message в String.
        out.add(str);// добавляем в список, т.е. отправляем объект String дальше следующему handler на обработку.
    }
}
