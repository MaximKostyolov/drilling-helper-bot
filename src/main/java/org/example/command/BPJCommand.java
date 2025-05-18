package org.example.command;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BPJCommand extends ServiceCommand {

    public BPJCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());

        if (strings.length % 3 != 0 || strings.length < 6) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, "Некорректные входные данные. " +
                    "Пожалуйста отправьте команду в виде: /bpj 3200 215.9 1.1 2100 168 8 2500 127 9.2 700 147 11 без всяких разделителей и единиц измерения. " +
                    "Первое число - глубина скважины в м. Второе число - диаметр долота в мм. Третье число - коэффициент кавернозности. " +
                    "Четвертое число - глубина спуска предыдущей колонны (если предыдущей колонны нет - отправлять 0). " +
                    "Пятое число - наружный диаметр предыдущей колонны в мм (если предыдущей колонны нет - отправлять 0). " +
                    "Шестое число - толщина стенки предыдущей колонны в мм (если предыдущей колонны нет - отправлять 0). " +
                    "Далее идет информация по секциям труб начиная от долота: длина секции труб в м диаметр секции труб в мм толщина секции труб в мм");
        } else {
            try {
                float H_skv = Float.parseFloat(strings[0]);
                float doloto = Float.parseFloat(strings[1]);
                float k = Float.parseFloat(strings[2]);

                float dlinnaKolonny = Float.parseFloat(strings[3]);
                float diametrKolonny = Float.parseFloat(strings[4]);
                float stenkaKolonny = Float.parseFloat(strings[5]);

                List<Sekcia> sekciaList = new ArrayList<>();

                for (int i = 6; i < strings.length; i = i+ 3) {
                    Sekcia sekcia = new Sekcia();
                    sekcia.setL(Float.parseFloat(strings[i]));
                    sekcia.setD(Float.parseFloat(strings[i + 1]));
                    sekcia.setN(Float.parseFloat(strings[i + 2]));
                    sekciaList.add(sekcia);
                }

                StringBuilder result = new StringBuilder("Получены данные: глубина скважины - " + H_skv + " м; диаметр долота - " + doloto + " мм; " +
                        "коэффициент кавернозности - " + k + "; глубина спуска предыдущей колонны - " + dlinnaKolonny + " м; " +
                        "наружный диаметр предыдущей колонны - " + diametrKolonny + " мм; толщина стенки предыдущей колонны - " +
                        stenkaKolonny + " мм;");

                if (!sekciaList.isEmpty()) {
                    result.append(" секции труб:");
                    for (Sekcia sekcia : sekciaList) {
                        result.append(" ( ").append(sekcia.getL()).append(" м; ").append(sekcia.getD()).append(" мм; ").append(sekcia.getN()).append(" мм;").append(" );");
                    }
                }

                result.append(System.lineSeparator());

                double S_kol = 3.14 * (diametrKolonny - (2 * stenkaKolonny)) * (diametrKolonny - (2 * stenkaKolonny)) / 4000000;
                double S_skv = 3.14 * k * doloto * doloto / 4000000;

                double V_skv = ( (S_skv * (H_skv - dlinnaKolonny)) + (S_kol * dlinnaKolonny) );
                DecimalFormat df = new DecimalFormat("#.##");
                result.append(System.lineSeparator());
                result.append("Объем скважины без инструмента: ").append(df.format(V_skv)).append(" м3;");

                if (!sekciaList.isEmpty()) {
                    double V_trub_pr = 0;
                    double V_trub = 0;
                    double V_zatrub = 0;

                    float result_L = 0;
                    for (Sekcia sekcia : sekciaList) {
                        result_L = result_L + sekcia.getL();

                        V_trub = V_trub + ( (getS_sek(sekcia) - getS_sek_vn(sekcia)) * sekcia.getL() );
                        V_trub_pr = V_trub_pr + ( getS_sek_vn(sekcia) * sekcia.getL() );
                    }

                    if (result_L > H_skv) {
                        result.append(System.lineSeparator());
                        result.append("Внимание: суммарная длина секций труб превышает длину скважины - проверьте входные данные. " +
                                "Расчет объемов производится для суммарной глубины спуска секций: ").append(result_L).append("м");
                    }
                    result.append(System.lineSeparator());
                    result.append("Объем металла труб для глубины ").append(result_L).append("м : ").append(df.format(V_trub)).append(" м3;");

                    result.append(System.lineSeparator());
                    result.append("Объем трубного пространства: ").append(df.format(V_trub_pr)).append(" м3;");

                    if (result_L > dlinnaKolonny) {
                        float L_otkr_stv = result_L - dlinnaKolonny;
                        for (Sekcia sekcia : sekciaList) {
                            if (L_otkr_stv > 0) {
                                if (sekcia.getL() >= L_otkr_stv) {
                                    V_zatrub = V_zatrub + ((S_skv - getS_sek(sekcia)) * L_otkr_stv);
                                    V_zatrub = V_zatrub + ((S_kol - getS_sek(sekcia)) * (sekcia.getL() - L_otkr_stv));
                                    L_otkr_stv = 0;
                                } else {
                                    V_zatrub = V_zatrub + ((S_skv - getS_sek(sekcia)) * sekcia.getL());
                                    L_otkr_stv = L_otkr_stv - sekcia.getL();
                                }
                            } else {
                                V_zatrub = V_zatrub + ( (S_kol - getS_sek(sekcia)) * sekcia.getL() );
                            }
                        }
                    } else {
                        for (Sekcia sekcia : sekciaList) {
                            V_zatrub = V_zatrub + ( (S_kol - getS_sek(sekcia)) * sekcia.getL() );
                        }
                    }
                    result.append(System.lineSeparator());
                    result.append("Объем затрубного пространства: ").append(df.format(V_zatrub)).append(" м3;");
                }

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

    private double getS_sek_vn(Sekcia sekcia) {
        return 3.14 * (sekcia.getD() - (2 * sekcia.getN())) * (sekcia.getD() - (2 * sekcia.getN())) / 4000000;
    }

    private double getS_sek(Sekcia sekcia) {
        return 3.14 * sekcia.getD() * sekcia.getD() / 4000000;
    }

    @Data
    static class Sekcia {
        private float l;
        private float d;
        private float n;
    }

}
