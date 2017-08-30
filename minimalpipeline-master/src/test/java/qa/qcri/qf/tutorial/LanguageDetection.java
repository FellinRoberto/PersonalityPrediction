package qa.qcri.qf.tutorial;

import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;

import org.xml.sax.SAXException;

public class LanguageDetection {

   public static void main(String args[])throws IOException, SAXException, TikaException {

      LanguageIdentifier identifier = new LanguageIdentifier("cleaning house...very slowly today haha.  nice to see family over the weekend. i miss them and wish they lived closer");
      String language = identifier.getLanguage();
      System.out.println("Language of the given content is : " + language);
   }
}