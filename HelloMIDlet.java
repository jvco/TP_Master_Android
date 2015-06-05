package hello;
// Code réalisé en binome avec ZIATI Jaouad
// Rendu de COSSU Jean-Valère uapv1001565
// Je n'utilise pas la classe PNG Encoder fournie car tout fonctionne correctement en JPEG
// Je ne gère pas l'affichage des photos stockées sur le téléphone ni leur suppression

// Initialisation des imports dont on aura besoin
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class HelloMIDlet extends MIDlet implements CommandListener
{
    private Command exitCommand = new Command("Exit", Command.EXIT, 0);// Bouton Exit
    private Command research = new Command("Research", Command.OK, 1);// Bouton Recherche
    private Command back;// Bouton Retour
    private Command back1 = new Command("Back", Command.BACK, 1);// Bouton Retour n°2
    private Command save = new Command("sauvegarder", Command.OK, 1);// Bouton Enregistrer
    private Command affich;// Bouton Affichage
    private Display display;// Affichage
    private Form FormInit;// Formulaire n°1
    private Form FormImages;// Formulaire n°2
    private Form FormImagesSaved;// Formulaire n°3
    private ChoiceGroup TailleSelect;// Liste pour le choix de la taille des images
    private TextField KeyWordTextField = new TextField("","",10,TextField.ANY);// TextField pour le mot clé de recherche
    private TextField NumberImageTextField =new TextField("","",10,TextField.ANY);// TextField pour le nombre d'image à afficher
    private List  ListImages;// Liste
    private int NbImages;// Variable pour le nombre d'image
    public Vector UrlVector ;// Vecteur pour le parse des images avec le DOS et le nombre d'images
    public int IndexImageSelected;// Index vecteur

    public HelloMIDlet()
    {
        // Affichage du bouton Exit
        display = Display.getDisplay(this);
        exitCommand = new Command("Exit", Command.EXIT, 0);

    }
    public void startApp()
    {
        // Lancement de l'application initialisation du formulaire
        InitForm();
    }

    public void pauseApp() {    }
    public void destroyApp(boolean unconditional) {    }

    public void commandAction(Command c, Displayable s)
    {
    // Fonction qui gère les boutons et les actions qui sont liées
	// Bouton Exit
        if (c == exitCommand)
        {
            // Detection du bouton Exit
            destroyApp(false);
            notifyDestroyed();
            // Fin de l'application
        }
        // Detection du bouton Recherche
        if (c == research)
			Search();
        // Detection du bouton Retour
        if (c == back)
			Display.getDisplay(this).setCurrent(FormInit);
        // Detection du bouton Retour
        if (c == back1)
			Display.getDisplay(this).setCurrent(FormImages);
        // Detection du bouton Afficher
        if (c == affich)
			AffichList();
		// Detection du bouton sauvegarde
        if (c == save )
            SaveImageSelected();
    }
    //fonction qui initialise le Form de départ
    public void InitForm()
	{
            FormInit = new Form("Flickr");// Gestion du forumalaire
            FormInit.append("Mot clé");// Création du formulaire général
            FormInit.append(KeyWordTextField);// Création du formulaire pour le mot clé
            FormInit.append("Nombre d'image");
            FormInit.append(NumberImageTextField);// Création du formulaire pour le nombre d'images
            TailleSelect = new ChoiceGroup("Taille de l\'image", Choice.EXCLUSIVE);            // Gestion de la liste pour le choix des tailles
            // Listes des choix
            TailleSelect.append("Thumbnail", null);
            TailleSelect.append("Small", null);
            TailleSelect.append("Medium", null);
            // La reconnaissance de la taille des images se fera ensuite par extraction du nom de l'image dans l'URL
            // (url qui finit par t.jpg pour un Thumbnail ou s.jpg pour une Small
            TailleSelect.setLayout(ImageItem.LAYOUT_LEFT | Item.LAYOUT_TOP | Item.LAYOUT_VCENTER);
            TailleSelect.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
            FormInit.append(TailleSelect);
            // On remet les boutons
            FormInit.addCommand(exitCommand);
            FormInit.addCommand(research);
            FormInit.setCommandListener(this);
            Display.getDisplay(this).setCurrent(FormInit);// Affichage
    }
    public void Search()
    {
            // Fonction recherche
            HttpConnection hc=null;// Initilisation des variables de connexions
            DataInputStream in = null;
            String key = KeyWordTextField.getString();// On récupère les mots clés
            FlickrHtmlParser FHP = new FlickrHtmlParser();// On initialise le parser fournit
            NbImages = Integer.parseInt(NumberImageTextField.getString());// On  récupère le nombre d'image à prendre
            String url = "http://www.flickr.com/search/?w=all&q="+key+"&m=text";// Url pour les recherches sur flickr
            try
            {
                hc = ( HttpConnection ) Connector . open ( url );// On ouvre la connexion au site
                in = hc . openDataInputStream () ;// On récupère les données
                UrlVector = FHP.parse(in, NbImages);// On traite la réponse en lançant le parseur
                FormImages = new Form("images pour: "+key);
                String URL;// URL de l'image
                int taille;// Taille de l'URL
                String URLDecoupee;
                // On récupère toutes les images
                // Premier cas on a choisi que les Thumbnail
                //si thumbnail est selectioné, on ne modifie pas le vecteur d'URL
                // Deuxième cas on a choisi que les Small
                if(TailleSelect.getSelectedIndex()==1)
                {
                    // On récupère toutes les images de ce type
                    for (int i=0; i<NbImages; i++)
                    {
                        URL = UrlVector.elementAt(i).toString();// On récupère la taille de l'URL
                        taille = URL.length();
                        URLDecoupee = URL.substring(0, taille-5);// On connait les 5 derniers caractères de l'URL
                        //on decoupe l'URL obtenu et on met s à la place du t
                        UrlVector.setElementAt(URLDecoupee+"s.jpg", i);// On vérifique que cela correspond
                        System.out.println("vecteur modifié: "+UrlVector.elementAt(i));
                    }
                }
                // Troisème cas on a choisi que les Medium
                if(TailleSelect.getSelectedIndex()==2)
                {
                    // On récupère toutes les images de ce type
                    for (int i=0; i<NbImages; i++)
                    {
                            URL = UrlVector.elementAt(i).toString();
                            taille = URL.length();// On récupère la taille de l'URL
                            URLDecoupee = URL.substring(0, taille-5);// On connait les 5 derniers caractères de l'URL
                            //on decoupe l'URL obtenu et on met m à la place du t
                            UrlVector.setElementAt(URLDecoupee+"m.jpg", i);// On vérifique que cela correspond
                            System.out.println("vecteur modifié: "+UrlVector.elementAt(i));
                    }
                }
                for (int i=0; i<NbImages; i++)
                {
                    Image image=null;
                    try
                    {
                        image = loadImage(UrlVector.elementAt(i).toString());
                    }
                    catch(Exception e)
                    {
                        System.err.println(e.getMessage());
                    }
                    FormImages.append(image);
                }
                back = new Command("back", Command.EXIT, 1);// On remet les boutons
                affich = new Command("lister" , Command.OK, 1);
                FormImages.addCommand(back);
                FormImages.addCommand(affich);
                FormImages.setCommandListener(this);
                Display.getDisplay(this).setCurrent(FormImages);// Affichage
                // Fermeture du DOS et de la connexion
                in . close () ;
                hc . close () ;
                }
            catch ( IOException ioe ) {  }

    }
    // Fonction pour charger les images
    public Image loadImage(String url) throws IOException
    {
        // Initialisation des paramètres de la connexion
        HttpConnection hpc = null;
        DataInputStream dis = null;
        try
        {
            hpc = (HttpConnection) Connector.open(url);// On ouvre la connexion au site
            int length = (int) hpc.getLength();
            byte[] data = new byte[length];
            dis = new DataInputStream(hpc.openInputStream());// Lecture des données
            dis.readFully(data);
            return Image.createImage(data, 0, data.length);// Retour de l'image créée
        }
        finally
        {
            if (hpc != null)
            hpc.close();// Fermture de la connexion
            if (dis != null)
            dis.close();// Fermeture du DIS
        }
    }
    // Fonction qui charge l'image en fonction de l'url
    public static byte[] loadImage2(String url) throws IOException
    {
        // Initialisation des paramètres de la connexion
        HttpConnection hpc = null;
        DataInputStream dis = null;
        try
        {
            hpc = (HttpConnection) Connector.open(url);
            int length = (int) hpc.getLength();
            byte[] data = new byte[length];
            dis = new DataInputStream(hpc.openInputStream());
            dis.readFully(data);
            return data;
        }
        finally
        {
            if (hpc != null)
            hpc.close();// Fermture de la connexion
            if (dis != null)
            dis.close();// Fermeture du DIS
        }
    }
    // Fonction d'affichage de la liste d'image
    public void AffichList()
    {
        ListImages = new List("images", Choice.EXCLUSIVE);//Affichage de la liste d'image
        for (int i = 0 ; i < NbImages ; i++)
        {
            try
            {
                ListImages.append("image n° "+Integer.toString(i), (Image) loadImage(UrlVector.elementAt(i).toString() ));
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
         }
        // On remet les boutons
        ListImages.addCommand(back1);
        ListImages.addCommand(save);
        ListImages.setCommandListener(this);
        Display.getDisplay(this).setCurrent(ListImages);// On affiche
     }
    // Fonction qui sauvegarde l'image selectionée
    public void SaveImageSelected()
    {
        try
        {
            String path="file:///root1/photos/"+KeyWordTextField.getString()+" "+ListImages.getSelectedIndex()+".jpg";// Chemin vers la carte mémoire ici root1/photos
            FileConnection fc=(FileConnection)Connector.open(path);// Connexion au dossier et création du fichier
            if(!fc.exists())
            fc.create();
            DataOutputStream dos = fc.openDataOutputStream();
            dos.write( loadImage2((String) UrlVector.elementAt(ListImages.getSelectedIndex())));// On remplit le fichier
            // On ferme le fichier et le DOS
            dos.close();
            fc.close();
         }
         catch( ConnectionNotFoundException error )
         {
             // Création des alertes en cas de problèmes
             Alert alert = new Alert("Error", "Cannot access file.", null, null);
             alert.setTimeout(Alert.FOREVER);
             alert.setType(AlertType.ERROR);
             display.setCurrent(alert);// Affichage
         }
         catch( IOException error )
         {
             // Création des alertes en cas de problèmes
             Alert alert = new Alert("Error", error.toString(), null, null);
             alert.setTimeout(Alert.FOREVER);
             alert.setType(AlertType.ERROR);
             display.setCurrent(alert);// Affichage
         }
    }
}