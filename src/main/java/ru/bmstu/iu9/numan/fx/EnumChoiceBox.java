package ru.bmstu.iu9.numan.fx;

import javafx.beans.NamedArg;
import javafx.scene.control.ChoiceBox;

public class EnumChoiceBox<E extends Enum<E>> extends ChoiceBox<E> {

    public EnumChoiceBox(@NamedArg("enumType") String enumType) throws Exception {
        try {
            Class<E> enumClass = (Class<E>) Class.forName(enumType);
            getItems().setAll(enumClass.getEnumConstants());
        } catch (Exception e) {
            System.out.println("failed to load enumType");
            e.printStackTrace();
            throw e;
        }
    }

}
