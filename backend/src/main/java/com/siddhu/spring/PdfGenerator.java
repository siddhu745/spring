package com.siddhu.spring;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.licensing.base.LicenseKey;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class PdfGenerator {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        LicenseKey.loadLicenseFile(new File("backend/key.json"));
        final String[] sources = {"backend/telugu.xml"};
        final PdfWriter writer = new PdfWriter("output.pdf");
        final PdfDocument pdfDocument = new PdfDocument(writer);
        final Document document = new Document(pdfDocument);
        final FontSet set = new FontSet();
//        set.addFont("fonts/NotoNaskhArabic-Regular.ttf");
//        set.addFont("fonts/NotoSansTamil-Regular.ttf");
        set.addFont("backend/NotoSansTelugu-Regular.ttf");
//        set.addFont("fonts/FreeSans.ttf");
        document.setFontProvider(new FontProvider(set));
        document.setProperty(Property.FONT, new String[]{"MyFontFamilyName"});
        for (final String source : sources) {
            final File xmlFile = new File(source);
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final org.w3c.dom.Document doc = builder.parse(xmlFile);
            final Node element = doc.getElementsByTagName("text").item(0);
            final Paragraph paragraph = new Paragraph();
            final Node textDirectionElement = element.getAttributes().getNamedItem("direction");
            boolean rtl = textDirectionElement != null && textDirectionElement.getTextContent()
                    .equalsIgnoreCase("rtl");
            if (rtl) {
                paragraph.setTextAlignment(TextAlignment.RIGHT);
            }
            paragraph.add(element.getTextContent());
            document.add(paragraph);
        }
        document.close();
        pdfDocument.close();
        writer.close();
    }
}
