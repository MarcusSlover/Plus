package tests;

import com.marcusslover.plus.lib.color.Color;
import com.marcusslover.plus.lib.color.ColorGradient;

public class ColorTest {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        ColorGradient gradient = ColorGradient.of(Color.of(0, 0, 0), Color.of(255, 255, 255));
        gradient.forEach(10, System.out::println);

    }
}
