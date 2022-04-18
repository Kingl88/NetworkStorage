package message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(//Аннотация, которая будет говорить что при сериализации объекта будет добавлять дополнительное поле "type"
        use = JsonTypeInfo.Id.MINIMAL_CLASS,//и туда будет записываться название класса (т.к. применяется MINIMAL_CLASS)
        property = "type"//это делается что бы правильно инициализировать объект типа Message
)
public abstract class Message {
}
