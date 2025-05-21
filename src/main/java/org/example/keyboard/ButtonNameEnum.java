package org.example.keyboard;

import lombok.Getter;

/**
 * Названия кнопок основной клавиатуры
 */
@Getter
public enum ButtonNameEnum {

    GNVP_BUTTON("Определить плотность БР при ГНВП"),
    BPJ_BUTTON("Рассчитать объемы скважины"),
    PUMPING_BUTTON("Рассчитать время прокачки пачки"),
    HELP_BUTTON("Помощь");

    private final String buttonName;

    ButtonNameEnum(String buttonName) {
        this.buttonName = buttonName;
    }

}
