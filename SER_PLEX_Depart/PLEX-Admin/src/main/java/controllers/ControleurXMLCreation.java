package controllers;

import ch.heigvd.iict.ser.imdb.models.Role;
import models.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import views.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
*/

import org.jdom2.*;

import com.thoughtworks.xstream.XStream;

public class ControleurXMLCreation {

    //private ControleurGeneral ctrGeneral;
    private static MainGUI mainGUI;
    private ORMAccess ormAccess;

    private GlobalData globalData;

    public ControleurXMLCreation(ControleurGeneral ctrGeneral, MainGUI mainGUI, ORMAccess ormAccess) {
        //this.ctrGeneral=ctrGeneral;
        ControleurXMLCreation.mainGUI = mainGUI;
        this.ormAccess = ormAccess;
    }

    public void createXML() {
        new Thread() {
            public void run() {
                mainGUI.setAcknoledgeMessage("Creation XML... WAIT");
                long currentTime = System.currentTimeMillis();
                try {
                    globalData = ormAccess.GET_GLOBAL_DATA();
                    //mainGUI.setWarningMessage("Creation XML: Fonction non encore implementee");

                    /////////////////////////////////////////////////

                    //Document document = new Document(new Element("projections"));
                    SAXBuilder builder = new SAXBuilder();
                    Document doc = builder.build(new File("global_data.xml"));

                    // Projection
                    String projectionName   = "projection";
                    String filmName         = "film";
                    String salleName        = "salle";
                    String dateHeureName    = "dateHeure";

                    // Film
                    String titreName        = "titre";
                    String synopsisName     = "synopsis";
                    String dureeName        = "duree";
                    String critiquesName    = "critiques";
                    String genresName       = "genres";
                    String motClesName      = "motCles";
                    String langagesName     = "langage";
                    String imageName        = "image";
                    String roleName         = "role";


                    List<Projection> projections = globalData.getProjections();

                    doc.setRootElement(new Element("projections"));

                    for(Projection projection : projections) {

                        ///////// Projection //////////
                        Element projectionElement = new Element(projectionName);
                        doc.getRootElement().addContent(projectionElement);

                        Element filmElement         = new Element(filmName);
                        Element salleElement        = new Element(salleName);
                        Element dateHeureElement    = new Element(dateHeureName);

                        projectionElement.setAttribute("id", String.valueOf(projection.getId()));
                        projectionElement.addContent(filmElement);
                        projectionElement.addContent(salleElement);
                        projectionElement.addContent(dateHeureElement);


                        ///////// Film //////////
                        Film  film  = projection.getFilm();
                        Element titreElement        = new Element(titreName);
                        Element synopsisElement      = new Element(synopsisName);
                        Element dureeElement        = new Element(dureeName);
                        Element critiquesElement    = new Element(critiquesName);
                        Element genresElement       = new Element(genresName);
                        Element motsClesElement     = new Element(motClesName);
                        Element langageElement      = new Element(langagesName);
                        Element imageElement        = new Element(imageName);

                        titreElement.setText(film.getTitre());
                        synopsisElement.setText(film.getSynopsis());
                        dureeElement.setText(String.valueOf(film.getDuree()));
                        imageElement.setText(film.getPhoto());

                        ///////// Critiques //////////
                        Set<Critique> critiques = film.getCritiques();
                        for(Critique critique : critiques) {

                        }

                        ///////// Genres //////////
                        Set<Genre> genres = film.getGenres();
                        for(Genre genre : genres) {

                        }

                        ///////// MotsCles //////////
                        Set<Motcle> motscles = film.getMotcles();
                        for(Motcle motcle : motscles) {

                        }

                        ///////// Langages //////////
                        Set<Langage> langages = film.getLangages();
                        for(Langage langage : langages) {

                        }

                        ///////// Roles //////////
                        Set<RoleActeur> roles = film.getRoles();
                        for(RoleActeur role : roles) {

                        }






                        Salle salle = projection.getSalle();            // salle de projection
                        Calendar dateHeure = projection.getDateHeure(); // date et heure de la projection

                    }

                    // Output XML
                    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

                    outputter.output(doc, System.out);

                    /////////////////////////////////////////////////

                } catch (Exception e) {
                    mainGUI.setErrorMessage("Construction XML impossible", e.toString());
                }
            }
        }.start();
    }

    public void createXStreamXML() {
        new Thread() {
            public void run() {
                mainGUI.setAcknoledgeMessage("Creation XML... WAIT");
                long currentTime = System.currentTimeMillis();
                try {
                    globalData = ormAccess.GET_GLOBAL_DATA();
                    globalDataControle();
                } catch (Exception e) {
                    mainGUI.setErrorMessage("Construction XML impossible", e.toString());
                }

                XStream xstream = new XStream();
                writeToFile("global_data.xml", xstream, globalData);
                System.out.println("Done [" + displaySeconds(currentTime, System.currentTimeMillis()) + "]");
                mainGUI.setAcknoledgeMessage("XML cree en " + displaySeconds(currentTime, System.currentTimeMillis()));
            }
        }.start();
    }

    private static void writeToFile(String filename, XStream serializer, Object data) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
            serializer.toXML(data, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final DecimalFormat doubleFormat = new DecimalFormat("#.#");

    private static final String displaySeconds(long start, long end) {
        long diff = Math.abs(end - start);
        double seconds = ((double) diff) / 1000.0;
        return doubleFormat.format(seconds) + " s";
    }

    private void globalDataControle() {
        for (Projection p : globalData.getProjections()) {
            System.out.println("******************************************");
            System.out.println(p.getFilm().getTitre());
            System.out.println(p.getSalle().getNo());
            System.out.println("Acteurs *********");
            for (RoleActeur role : p.getFilm().getRoles()) {
                System.out.println(role.getActeur().getNom());
            }
            System.out.println("Genres *********");
            for (Genre genre : p.getFilm().getGenres()) {
                System.out.println(genre.getLabel());
            }
            System.out.println("Mot-cles *********");
            for (Motcle motcle : p.getFilm().getMotcles()) {
                System.out.println(motcle.getLabel());
            }
            System.out.println("Langages *********");
            for (Langage langage : p.getFilm().getLangages()) {
                System.out.println(langage.getLabel());
            }
            System.out.println("Critiques *********");
            for (Critique critique : p.getFilm().getCritiques()) {
                System.out.println(critique.getNote());
                System.out.println(critique.getTexte());
            }
        }
    }
}



