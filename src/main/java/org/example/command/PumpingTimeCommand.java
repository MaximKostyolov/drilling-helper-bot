package org.example.command;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.text.DecimalFormat;

public class PumpingTimeCommand extends ServiceCommand {

    public PumpingTimeCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());

        if (strings.length != 5) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, "Некорректные входные данные. " +
                    "Пожалуйста отправьте команду в виде: /pumping 32 30.54 37.65 1 5 без всяких разделителей и единиц измерения. " +
                    "Первое число - производительность буровых насосов в л/c. Второе число - объём трубного пространства в м3. " +
                    "Третье число - объём затрубного пространства в м3. Четвертое число - объём наружней обвязки в м3; " +
                    "Пятое число - объём пачки в м3.");
        } else {
            try {
                float Q = Float.parseFloat(strings[0]);
                float V_tr = Float.parseFloat(strings[1]);
                float V_ztr = Float.parseFloat(strings[2]);

                float V_obv = Float.parseFloat(strings[3]);
                float V_pch = Float.parseFloat(strings[4]);

                StringBuilder result = new StringBuilder("Получены данные: производительность буровых насосов - " + Q +
                        " л/с; объём трубного пространства - " + V_tr + " м3; объём затрубного пространства - " + V_ztr +
                        "м3; объём наружней обвязки - " + V_obv + " м3; объём пачки - " + V_pch + "м3");

                result.append(System.lineSeparator());

                double t_kol = (V_tr+V_obv) * 1000 / (Q * 60) ;
                double t_pch = V_pch * 1000 / (Q * 60) ;
                double t_ztr = V_ztr * 1000 / (Q * 60);
                double t_sm = t_ztr+t_kol;
                double t_vh = t_sm+t_pch;

                DecimalFormat df = new DecimalFormat("#.##");
                result.append(System.lineSeparator());
                result.append("время начала выхода пачки в затрубное пространство: ").append(df.format(t_kol)).append(" мин;");
                result.append(System.lineSeparator());
                result.append("время прокачки затрубного пространства: ").append(df.format(t_ztr)).append(" мин;");
                result.append(System.lineSeparator());
                result.append("время начала выхода пачки на устье скважины: ").append(df.format(t_sm)).append(" мин;");
                result.append(System.lineSeparator());
                result.append("время полного выхода пачки на устье скважины: ").append(df.format(t_vh)).append(" мин;");

                sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, result.toString());
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
