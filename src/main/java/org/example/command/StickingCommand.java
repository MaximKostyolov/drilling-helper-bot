package org.example.command;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StickingCommand extends ServiceCommand {

    public StickingCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());

        if (strings.length != 4) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, "Некорректные входные данные. " +
                    "Пожалуйста отправьте команду в виде: /sticking вверх свободно затруднено невозможна без всяких разделителей и единиц измерения. " +
                    "Первое слово - Движение колонны неподсредственно перед прихватом (вверх ,вниз, отсутствует). " +
                    "Второе слово - Идет ли колонна вниз после получения прихвата (свободно, затруднен, невозможно). " +
                    "Третье слово - Как характеризуется вращение колонны после получения прихвата (свободно, затруднено, невозможно). " +
                    "Четвертое слово - Какой характер носит циркуляция после получения прихвата (свободна, ограничена, невозможна). ");
        } else {
            try {
                String dvizjenie = strings[0];
                String vniz = strings[1];
                String vrashenie = strings[2];
                String cirkul = strings[3];

                if (!validateValues(dvizjenie, vniz, vrashenie, cirkul)) {
                    sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, "Некорректные входные данные. Допустимые значения: " +
                            "Первое слово - вверх ,вниз, отсутствует; " +
                            "Второе слово - свободно, затруднен, невозможно; " +
                            "Третье слово - свободно, затруднено, невозможно; " +
                            "Четвертое слово - свободна, ограничена, невозможна");
                } else {

                    int summa1 = getSumma1(dvizjenie, vniz, vrashenie, cirkul);
                    int summa2 = getSumma2(dvizjenie, vniz, vrashenie, cirkul);
                    int summa3 = getSumma3(dvizjenie, vniz, vrashenie, cirkul);

                    StringBuilder result = new StringBuilder("Получены данные: Движение колонны неподсредственно перед прихватом - " + dvizjenie +
                            " Идет ли колонна вниз после получения прихвата - " + vniz +
                            " Как характеризуется вращение колонны после получения прихвата - " + vrashenie +
                            " Какой характер носит циркуляция после получения прихвата - " + cirkul);

                    result.append(System.lineSeparator());

                    result.append("Сумма 1 = " + summa1);
                    result.append(System.lineSeparator());
                    result.append("Сумма 2 = " + summa2);
                    result.append(System.lineSeparator());
                    result.append("Сумма 3 = " + summa3);
                    result.append(System.lineSeparator());

                    if (summa1 > summa2 && summa1 > summa3) {
                        result.append("Тип прихвата: Прихват в шламовой пробке или из-за осыпи и обвала скважины");
                        result.append(System.lineSeparator());
                        result.append("1. Сбросить давление, возросшее из-за образования пробки, а потом создать небольшое " +
                                "давление (слишком большое давление вдавит КНБК, как поршень, дальше в пробку). " +
                                "Небольшое давление требуется для того, чтобы восстановить циркуляцию, если удастся сдвинуть колонну с места");
                        result.append(System.lineSeparator());

                        result.append("2. Приложить крутящий момент и произвести удар вниз ГУМ (Гидроударным механизмом), " +
                                "например Яс или ВУК. Если ГУМ не включен в компоновку или не работает, приложить крутящий " +
                                "момент и максимальную осевую нагрузку (чтобы сдвинуть колонну в направлении, противоположном" +
                                " тому, в котором она двигалась до прихвата. Если попытаться приподнять колонну , то она еще " +
                                "дальше зайдет в пробку. Цель заключается в том, чтобы сместить колонну и восстановить циркуляцию," +
                                " чтобы размыть пробку и вынести материал пробки вверх по стволу.  Следует отметить, что если ко " +
                                "времени  возникновения прихвата колонну перемещали вниз в с высоким углом наклона ствола скважины," +
                                " то нужно попытаться приподнять ее или произвести удар ГУМ вверх, без вращения");
                        result.append(System.lineSeparator());
                        result.append("3. Если удастся восстановить циркуляцию в какой-то степени, нужно увеличить расход до " +
                                "максимума, который возможен без поглощения, не допускать превышения начального давления поглощения. " +
                                "Продолжать циркуляцию, пока скважина не будет очищена");
                        result.append(System.lineSeparator());
                        result.append("4. Проработать интервал прихвата и вернуть инструмент на забой, промыть скважину перед " +
                                "спуском обсадной колонны или скважинных приборов");

                    } else if (summa2 > summa1 && summa2 > summa3) {
                        result.append("Тип прихвата: Дифференциальный прихват");
                        result.append(System.lineSeparator());
                        result.append("1. Произвети однократное  натяжение бурильной колонны, превышающее расчётные значения " +
                                "не менее  10тн. и более превышающее расчётное значение прижимной силы для интервала нахождения " +
                                "проницаемого пласта по вертикальной глубине  и давлению на подошве пласта, но не допуская превышения " +
                                "критических значений на разрыв бурильной колонны (наиболее слабого элемента) или веса на крюке талевого " +
                                "блока.  Расчётное значение   - рассчитываеться буровым супервайзером и доводиться буровому мастеру" +
                                " и бурильщику при заступлении на вахту при проведении планёрки перед вахтой");
                        result.append(System.lineSeparator());
                        result.append("2. Немедленно приложить максимальный крутящий момент и довести его до места прихвата");
                        result.append(System.lineSeparator());
                        result.append("3. Продолжить циркуляцию с минимальным  расходом, выполнять одновременно с приложение " +
                                "крутящего момента. Если в компоновку включен ГУМ, то на время удара вниз снизить подачу насоса " +
                                "до минимума, чтобы не противодействовать удару");
                        result.append(System.lineSeparator());
                        result.append("4. Поддерживая крутящий момент, резко разгрузить колонну, создавая максимальную осевую " +
                                "нагрузку. Ни в коем случае нельзя пытаться приподнять колонну! (это приведет только к осложнению " +
                                "прихвата, а натяжение колонны уменьшит значения крутящего момента, который можно безопасно приложить" +
                                " к колонне)");
                        result.append(System.lineSeparator());
                        result.append("5. Если в колонне есть ГУМ, нужно произвести удар вниз (не забывать снизить подачу насоса до " +
                                "минимума, чтобы не ослабить удар)");
                        result.append(System.lineSeparator());

                    } else if (summa3 > summa1 && summa3 > summa2) {
                        result.append("Тип прихвата: Заклинивание на участке сложной геометрии по длине ствола или  сужении ствола ( эллипс, желоб)");
                        result.append(System.lineSeparator());
                        result.append("1. Произвести удар ГУМ в направлении, противоположном тому, в котором двигалась колонна до " +
                                "прихвата. Приложить крутящий момент при ударах вниз.  Никогда не прикладывать крутящий момент " +
                                "при ударах вверх");
                        result.append(System.lineSeparator());
                        result.append("2. Не нужно забывать про давление буровой промывочной жидкости  при заряжании ГУМ или " +
                                "нанесении ударов. При увеличении этого давления удар ГУМ вверх усиливается, а удар вниз ослабляется." +
                                " Оно же мешает заряжанию ГУМ для удара вверх и помогает заряжанию для удара вниз");
                        result.append(System.lineSeparator());

                    } else if (summa3 == summa1) {
                        result.append("Тип прихвата: Прихват в шламовой пробке или из-за осыпи и обвала скважины и " +
                                    "Заклинивание на участке сложной геометрии по длине ствола или  сужении ствола ( эллипс, желоб)" +
                                    "Проверьте входные данные");
                    } else if (summa3 == summa2) {
                        result.append("Тип прихвата: Дифференциальный прихват и " +
                                "Заклинивание на участке сложной геометрии по длине ствола или  сужении ствола ( эллипс, желоб)" +
                                "Проверьте входные данные");
                    } else if (summa1 == summa2) {
                        result.append("Тип прихвата: Дифференциальный прихват и " +
                                "Прихваты в шламовой пробке или из-за осыпи и обвала скважины" +
                                "Проверьте входные данные");
                    }

                    sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, result.toString());
                }
            } catch(Exception exception) {
                sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, "Во время расчета произошла ошибка. " +
                        "Пожалуйста проверьте входные данные");
            }
        }
    }

    private boolean validateValues(String dvizjenie, String vniz, String vrashenie, String cirkul) {
        if (!dvizjenie.equals("вверх") && !dvizjenie.equals("вниз") && !dvizjenie.equals("отсутствует")) {
            return false;
        }
        if (!vniz.equals("свободно") && !vniz.equals("затруднен") && !vniz.equals("невозможно")) {
            return false;
        }
        if (!vrashenie.equals("свободно") && !vrashenie.equals("затруднено") && !vrashenie.equals("невозможно")) {
            return false;
        }
        if (!cirkul.equals("свободна") && !cirkul.equals("ограничена") && !cirkul.equals("невозможна")) {
            return false;
        }
        return true;
    }

    private int getSumma3(String dvizjenie, String vniz, String vrashenie, String cirkul) {
        int summa3 = 0;
        if (dvizjenie.equals("вверх")) {
            summa3=2;
        } else if (dvizjenie.equals("вниз")) {
            summa3=2;
        } else if (dvizjenie.equals("отсутствует")) {
            summa3=0;
        }

        if (vniz.equals("свободно")) {
            summa3=summa3+2;
        } else if (vniz.equals("затруднен")) {
            summa3=summa3+2;
        } else if (vniz.equals("невозможно")) {
            summa3=summa3+0;
        }

        if (vrashenie.equals("свободно")) {
            summa3=summa3+2;
        } else if (vrashenie.equals("затруднено")) {
            summa3=summa3+2;
        } else if (vrashenie.equals("невозможно")) {
            summa3=summa3+0;
        }

        if (cirkul.equals("свободна")) {
            summa3=summa3+2;
        } else if (cirkul.equals("ограничена")) {
            summa3=summa3+0;
        } else if (cirkul.equals("невозможна")) {
            summa3=summa3+0;
        }
        return summa3;
    }

    private int getSumma2(String dvizjenie, String vniz, String vrashenie, String cirkul) {
        int summa2 = 0;
        if (dvizjenie.equals("вверх")) {
            summa2=0;
        } else if (dvizjenie.equals("вниз")) {
            summa2=0;
        } else if (dvizjenie.equals("отсутствует")) {
            summa2=2;
        }

        if (vniz.equals("свободно")) {
            summa2=summa2+0;
        } else if (vniz.equals("затруднен")) {
            summa2=summa2+0;
        } else if (vniz.equals("невозможно")) {
            summa2=summa2+0;
        }

        if (vrashenie.equals("свободно")) {
            summa2=summa2+0;
        } else if (vrashenie.equals("затруднено")) {
            summa2=summa2+0;
        } else if (vrashenie.equals("невозможно")) {
            summa2=summa2+0;
        }

        if (cirkul.equals("свободна")) {
            summa2=summa2+2;
        } else if (cirkul.equals("ограничена")) {
            summa2=summa2+0;
        } else if (cirkul.equals("невозможна")) {
            summa2=summa2+0;
        }
        return summa2;
    }

    private int getSumma1(String dvizjenie, String vniz, String vrashenie, String cirkul) {
        int summa1 = 0;
        if (dvizjenie.equals("вверх")) {
            summa1=2;
        } else if (dvizjenie.equals("вниз")) {
            summa1=1;
        } else if (dvizjenie.equals("отсутствует")) {
            summa1=2;
        }

        if (vniz.equals("свободно")) {
            summa1=summa1+0;
        } else if (vniz.equals("затруднен")) {
            summa1=summa1+1;
        } else if (vniz.equals("невозможно")) {
            summa1=summa1+0;
        }

        if (vrashenie.equals("свободно")) {
            summa1=summa1+0;
        } else if (vrashenie.equals("затруднено")) {
            summa1=summa1+2;
        } else if (vrashenie.equals("невозможно")) {
            summa1=summa1+0;
        }

        if (cirkul.equals("свободна")) {
            summa1=summa1+0;
        } else if (cirkul.equals("ограничена")) {
            summa1=summa1+2;
        } else if (cirkul.equals("невозможна")) {
            summa1=summa1+2;
        }
        return summa1;
    }

}
