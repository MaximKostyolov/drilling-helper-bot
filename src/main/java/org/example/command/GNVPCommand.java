package org.example.command;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.text.DecimalFormat;

public class GNVPCommand extends ServiceCommand {

    public GNVPCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());

        if (strings.length != 3) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, "Некорректные входные данные. " +
                    "Пожалуйста отправьте команду в виде: /gnvp 1.0 1150 2345 без всяких разделителей и единиц измерения. " +
                    "Первое число - избыточное давление в МПа. Второе число - текущая плотность бурового раствора в кг/м3. " +
                    "Третье число - глубина скважины по вертикали в м.");
        } else {
            try {
                float pressure = Float.parseFloat(strings[0]);
                float destiny = Float.parseFloat(strings[1]);
                float h = Float.parseFloat(strings[2]);

                String result = "Получены данные: избыточное давление - " + pressure + " МПа; текущая плотность бурового раствора - " +
                        destiny + " кг/м3; глубина скважины по вертикали - " + h + " м.";

                double resultDestiny = (pressure * 1000000 + destiny * h * 9.81) / (h * 9.81);
                DecimalFormat df = new DecimalFormat("#.##");
                String roundedDestiny = df.format(resultDestiny);
                result = result + System.lineSeparator() + System.lineSeparator() + "Требуемая плотность бурового раствора: " + roundedDestiny + " кг/м3";

                if (resultDestiny > 1500) {
                    result = result + " Требуемая плотность более 1500 кг/м3 - пожалуйста проверьте входные данные";
                }

                if (destiny < 1000) {
                    result = result + " Текущая плотность бурового раствора менее 1000 кг/м3 - пожалуйста проверьте входные данные";
                }

                sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, result);
            } catch (NumberFormatException exception) {
                sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, "Некорректный ввод данных. " +
                        "Пожалуйста введите дробные числа через точку");
            } catch (Exception exception) {
                sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, "Во время расчета произошла ошибка. " +
                        "Пожалуйста проверьте входные данные");
            }
        }
    }

}
