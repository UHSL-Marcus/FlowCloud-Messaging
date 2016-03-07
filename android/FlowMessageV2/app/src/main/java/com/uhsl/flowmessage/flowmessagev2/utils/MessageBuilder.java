package com.uhsl.flowmessage.flowmessagev2.utils;

import android.provider.DocumentsContract;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Marcus on 02/03/2016.
 */
public class MessageBuilder {

    public static String buildMessage(MessageFormat input) {

        String xmlString = null;

        System.out.println("Build message");

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("message");
            doc.appendChild(root);




            root.appendChild(newElement("messageID", input.messageID, doc));
            root.appendChild(newElement("senderID", input.senderID, doc));
            root.appendChild(newElement("senderType", input.senderType, doc));
            root.appendChild(newElement("type", input.type, doc));
            root.appendChild(newElement("body",input.body, doc));

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            OutputStream os = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(os);

            transformer.transform(source, result);

            xmlString = os.toString();



        } catch (Exception e) {
            // do something
            System.out.println("build error: " + e.toString() + " -> " + e.getMessage());
        }

        if (xmlString != null)
            System.out.println(xmlString);

        return xmlString;
    }

    private static Element newElement(String name, String text, Document doc) {
        Element e = doc.createElement(name);
        e.appendChild(doc.createTextNode(text));
        return e;
    }

    public static MessageFormat parseMessage(String input) {

        MessageFormat parsedMessage = new MessageFormat();

        System.out.println("parse message\n" + input);

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(input)));

            doc.getDocumentElement().normalize();

            Element element = doc.getDocumentElement();

            if (element.getNodeName().equals("message")) {

                parsedMessage.messageID = element.getElementsByTagName("messageID").item(0).getTextContent();
                parsedMessage.senderID = element.getElementsByTagName("senderID").item(0).getTextContent();
                parsedMessage.senderType = element.getElementsByTagName("senderType").item(0).getTextContent();
                parsedMessage.type = element.getElementsByTagName("type").item(0).getTextContent();
                parsedMessage.body = element.getElementsByTagName("body").item(0).getTextContent();
            }


        } catch (Exception e) {
            //do something
            System.out.println("parse error: " + e.toString() + " -> " + e.getMessage());
        }


        return  parsedMessage;
    }
}
