package services.GUI;

import java.awt.*;

/**
 * Created by ValentinC on 15/12/2015.
 */
class Wizz {
    //on passe en paramètre
    // 1: le composant(la fenêtre) sur lequel l'on doit faire le wizz
    // 2: vitesse elle donne l'allure du wizz pour un wizz comme MSN Messenger <40
    // 3: decallage le decallage du wizz (idéal 2 :-) )
    static void creerWizz(Component c, int vitesse, int decallage) {
        //position de la fenêtre à l'écran vous devez avoir préalablement choisi sa position
        // avec setbound()
        int pos_x=c.getBounds().getLocation().x;
        int pos_y=c.getBounds().getLocation().y;
        //tableau de décallage
        //position initiale et le decallage
        int[] pos={0,decallage};
        //fait bouger la fenêtre 12 fois
        for (int i=1;i<13;i++) {
            //fait un division si le reste de la division (mod) est 0 position initial
            //sinon position + decallage
            c.setBounds(pos_x+pos[i%2],pos_y+pos[i%2],c.getBounds().getSize().width,c.getBounds().getSize().height);
            try {
                //donne l'effet de wizz
                Thread.sleep(vitesse);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
