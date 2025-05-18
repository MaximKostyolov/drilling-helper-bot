package org.example.command;

/**
 * Обработка сообщения, не являющегося командой (т.е. обычного текста не начинающегося с "/")
 */
public class NonCommand {

    public String nonCommandExecute(Long chatId, String userName, String text) {
        String answer;
        try {
            answer = "Получен запрос: " + text;
            answer = answer + System.lineSeparator() + "К сожалению, данная функция находится еще на стадии разработки. " +
            "Воспользуйтесь пока другими функциями бота (команды /gnvp, /bpj, /pumping)";
        } catch (Exception e) {
            answer = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
        }
        return answer;
    }

}
