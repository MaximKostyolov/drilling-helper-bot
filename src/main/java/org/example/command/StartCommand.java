package org.example.command;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Команда "Старт"
 */
public class StartCommand extends ServiceCommand {

    public StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
        //обращаемся к методу суперкласса для отправки пользователю ответа
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Добро пожаловать в бот! Этот бот поможет вам найти информацию по бурению - просто введите интересующий вас запрос. " +
                        "Также бот может определить требуемую плотность бурового раствора по избыточному давлению при ГНВП (команда /gnvp), " +
                        "поможет посчитает объемы скважины, металла труб, трубного и затрубного пространства (команда /bpj). " +
                        "Если Вам нужна помощь, нажмите /help");
    }

}
