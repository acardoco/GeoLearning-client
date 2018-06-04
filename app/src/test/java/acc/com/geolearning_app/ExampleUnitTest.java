package acc.com.geolearning_app;

import org.junit.Test;

import acc.com.geolearning_app.dto.Nodo;
import acc.com.geolearning_app.util.utils;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void circulo(){
        int radioi =  5;
        int ai =  6;
        int bi = 6;

        double a = (double)ai;
        double b = (double)bi;
        double radio = (double)radioi;

        double angulo = 0;


        // nodo inicial
        Nodo nodo_ini = new Nodo();
        //meter 15 nodos restantes
        for (int p = 1; p <= 16; p++){
            //16 lados, 4 por cuadrante, r/4

            double a_centro = a + radio * Math.cos(angulo);
            double b_centro = b + radio * Math.sin(angulo);

            System.out.print("a_centro:" + String.valueOf(a_centro));
            System.out.println("-b_centro:" + String.valueOf(b_centro));

            angulo+=(22.5*Math.PI)/180;
        }
        assertEquals(4, 2 + 2);
    }
}