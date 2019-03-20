package me.shedaniel.cloth.gui.entries;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.Optional;
import java.util.function.Function;

public class FloatListEntry extends TextFieldListEntry<Float> {
    
    private static Function<String, String> stripCharacters = s -> {
        StringBuilder stringBuilder_1 = new StringBuilder();
        char[] var2 = s.toCharArray();
        int var3 = var2.length;
        
        for(int var4 = 0; var4 < var3; ++var4)
            if (Character.isDigit(var2[var4]) || var2[var4] == '-' || var2[var4] == '.')
                stringBuilder_1.append(var2[var4]);
        
        return stringBuilder_1.toString();
    };
    private float minimum, maximum;
    
    public FloatListEntry(String fieldName, Float value) {
        super(fieldName, value);
        this.minimum = Float.MIN_VALUE;
        this.maximum = Float.MAX_VALUE;
        this.textFieldWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 148, 18) {
            @Override
            public void addText(String string_1) {
                super.addText(stripCharacters.apply(string_1));
            }
            
            @Override
            public void draw(int int_1, int int_2, float float_1) {
                try {
                    float i = Float.valueOf(textFieldWidget.getText());
                    if (i < minimum || i > maximum)
                        method_1868(16733525);
                    else
                        method_1868(14737632);
                } catch (NumberFormatException ex) {
                    method_1868(16733525);
                }
                super.draw(int_1, int_2, float_1);
            }
        };
        textFieldWidget.setText(String.valueOf(value));
        textFieldWidget.setMaxLength(999999);
        textFieldWidget.setChangedListener(s -> {
            if (!original.equals(s))
                ((ClothConfigScreen.ListWidget) getParent()).getScreen().setEdited(true);
        });
    }
    
    public FloatListEntry setMinimum(float minimum) {
        this.minimum = minimum;
        return this;
    }
    
    public FloatListEntry setMaximum(float maximum) {
        this.maximum = maximum;
        return this;
    }
    
    @Override
    public Object getObject() {
        try {
            return Float.valueOf(textFieldWidget.getText());
        } catch (Exception e) {
            return 0f;
        }
    }
    
    @Override
    public Optional<String> getError() {
        try {
            float i = Float.valueOf(textFieldWidget.getText());
            if (i > maximum)
                return Optional.of(I18n.translate("text.cloth.error.too_large", maximum));
            else if (i < minimum)
                return Optional.of(I18n.translate("text.cloth.error.too_small", minimum));
        } catch (NumberFormatException ex) {
            return Optional.of(I18n.translate("text.cloth.error.not_valid_number_float"));
        }
        return super.getError();
    }
}