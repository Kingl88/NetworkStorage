package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import message.Message;

import java.util.List;

public class JsonDecoder extends MessageToMessageDecoder<String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();//класс позволяющий преобразовывать из JSON и в JSON.

    protected void decode(ChannelHandlerContext channelHandlerContext, String msg, List<Object> out) throws Exception {
        Message message = OBJECT_MAPPER.readValue(msg, Message.class);//читаем строку и говорим, что эту строку надо преобразовать в объект типа Message.
        out.add(message);// добавляем в список, т.е. отправляем объект String дальше следующему handler на обработку.
    }
}
