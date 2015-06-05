package Calculatrice;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class Calculatrice extends MIDlet implements CommandListener {

    private Command exitCommand; // The exit command
    private Command valider;    // The validation command
    private Display display;     // The display for this MIDlet
    String nb10 = new String();
    String nb20 = new String();
    char ope = ' ';
    TextBox t = new TextBox("Calculatrice", "", 256, 0);
    TextField nb = new TextField("", "", 10, 2);
    TextField resultat = new TextField("Résulat", "", 10, 2);

    public Calculatrice() {
        display = Display.getDisplay(this);
        exitCommand = new Command("Exit", Command.EXIT, 0);
        valider = new Command("valider", Command.OK, 0);
    }

    Alert alt = new Alert("Erreur", "Chaine saisie incorrecte", null, null);

    public void startApp() {
        t.addCommand(exitCommand);
        t.addCommand(valider);
        t.setCommandListener(this);

        display.setCurrent(t);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void reaction(){
        char data[] = null;
        nb.getChars(data);
        int i=0;
        int j=0;
        while(true){
            if (data[i]==' ') break;
            while (data[i]!='*' && data[i]!='+' && data[i]!='-' && data[i]!='/') {
                   i++;
                   ope=data[i];
            }
            for(int a=j;a!=i;a++)
            {
                nb10+=data[a];
            }
            i++;
            while(data[i]!=' ')
            {
                nb20+=data[i];
                i++;
            }
        }
    }

    public void commandAction(Command c, Displayable s) {
        if (c == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        }
        else if (c == valider) {
             reaction();
             int i = Integer.parseInt(nb10);
             int j = Integer.parseInt(nb20);

             int z=Calcul(i,j,ope);
             String result=""+z;
             char resulta[] = null;
             result.getChars(0, 3, resulta, 0);
             resultat.setChars(resulta, i, i);

             Display.getDisplay(this).setCurrent(t);
        }
    }

    public int Calcul(int i1, int i2, char op) {
                if (op == '*' ){
                    return (i1*i2);
                }
                else if (op == '-' ){
                    return i1*i2;
                }
                else if (op == '+' ) {
                     return i1+i2;
                }
                else if (op == '/' ){
                     return i1/i2;
                }
                else return 0;
    }
}