package android.support.test.espresso.web.assertion;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.transform.TransformerFactory;

public class JSoupDocumentParser {

    private static final ThreadLocal<TransformerFactory> transformerFactory =
            new ThreadLocal<TransformerFactory>() {
                @Override
                protected TransformerFactory initialValue() {
                    return TransformerFactory.newInstance();
                }
            };

    public Document parse(String html) throws SAXException, IOException {
        org.jsoup.nodes.Document doc = Jsoup.parse(html);
        return new W3CDom().fromJsoup(doc);
    }
}
