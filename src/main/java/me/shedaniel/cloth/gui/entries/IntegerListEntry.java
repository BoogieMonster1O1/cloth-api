package me.shedaniel.cloth.gui.entries;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IntegerListEntry extends TextFieldListEntry<Integer> {
    
    private static Function<String, String> stripCharacters = s -> {
        StringBuilder stringBuilder_1 = new StringBuilder();
        char[] var2 = s.toCharArray();
        int var3 = var2.length;
        
        for(int var4 = 0; var4 < var3; ++var4)
            if (Character.isDigit(var2[var4]) || var2[var4] == '-')
                stringBuilder_1.append(var2[var4]);
        
        return stringBuilder_1.toString();
    };
    private int minimum, maximum;
    private Consumer<Integer> saveConsumer;
    
    public IntegerListEntry(String fieldName, Integer value, Consumer<Integer> saveConsumer) {
        this(fieldName, value, "text.cloth-config.reset_value", null, saveConsumer);
    }
    
    public IntegerListEntry(String fieldName, Integer value, String resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer) {
        super(fieldName, value, resetButtonKey, defaultValue);
        this.minimum = -Integer.MAX_VALUE;
        this.maximum = Integer.MAX_VALUE;
        this.saveConsumer = saveConsumer;
    }
    
    @Override
    protected String stripAddText(String s) {
        return stripCharacters.apply(s);
    }
    
    @Override
    protected void textFieldPreRender(TextFieldWidget widget) {
        try {
            double i = Integer.valueOf(textFieldWidget.getText());
            if (i < minimum || i > maximum)
                widget.method_1868(16733525);
            else
                widget.method_1868(14737632);
        } catch (NumberFormatException ex) {
            widget.method_1868(16733525);
        }
    }
    
    @Override
    protected boolean isMatchDefault(String text) {
        return getDefaultValue().isPresent() ? text.equals(defaultValue.get().toString()) : false;
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getObject());
    }
    
    public IntegerListEntry setMaximum(int maximum) {
        this.maximum = maximum;
        return this;
    }
    
    public IntegerListEntry setMinimum(int minimum) {
        this.minimum = minimum;
        return this;
    }
    
    @Override
    public Integer getObject() {
        try {
            return Integer.valueOf(textFieldWidget.getText());
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public Optional<String> getError() {
        try {
            int i = Integer.valueOf(textFieldWidget.getText());
            if (i > maximum)
                return Optional.of(I18n.translate("text.cloth-config.error.too_large", maximum));
            else if (i < minimum)
                return Optional.of(I18n.translate("text.cloth-config.error.too_small", minimum));
        } catch (NumberFormatException ex) {
            return Optional.of(I18n.translate("text.cloth-config.error.not_valid_number_int"));
        }
        return super.getError();
    }
}
