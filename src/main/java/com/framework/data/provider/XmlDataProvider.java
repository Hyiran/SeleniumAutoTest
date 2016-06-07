package com.framework.data.provider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlDataProvider extends DataProviderImpl
{
  private static final String CASENODETAG = "case";
  private static final String ROWNODETAG = "row";
  private static final String DATANODETAG = "data";
  private static final String TYPETAG = "type";
  private static final String VALUETAG = "value";
  private static final String SPILTTAG = "spilt";

  protected void generateData(String caseName, String dataFile)
  {
    NodeList caseList = getNodeList(dataFile);
    if ((caseList.equals(null)) || (caseList.getLength() == 0)) {
      return;
    }
    for (int i = 0; i < caseList.getLength(); i++) {
      Node caseNode = caseList.item(i);
      if ((caseNode.getNodeType() != 1) || (!caseNode.getNodeName().equals("case")) || (!caseNode.getAttributes().getNamedItem("name").getNodeValue().equals(caseName)))
        continue;
      int ii = 1;
      for (Node n = caseNode.getFirstChild(); n != null; n = n.getNextSibling()) {
        if ((n.getNodeType() == 1) && (n.getNodeName().equals("row"))) {
          if ((ii >= this.start) && (ii < this.max)) {
            List rowdatas = new ArrayList();
            for (Node m = n.getFirstChild(); m != null; m = m.getNextSibling()) {
              if ((m.getNodeType() == 1) && (m.getNodeName().equals("data"))) {
                rowdatas.add(getRowDataByType(m));
              }
            }
            if (rowdatas.size() > 0) this.data.add(rowdatas.toArray());
            rowdatas.clear();
          }
          ii++;
        }
      }
      break;
    }
  }

  private Object getRowDataByType(Node node)
  {
    Node type = node.getAttributes().getNamedItem("type");
    if (type == null) {
      return node.getAttributes().getNamedItem("value").getNodeValue();
    }
    if (type.getNodeValue().equalsIgnoreCase("int")) {
      return Integer.valueOf(node.getAttributes().getNamedItem("value").getNodeValue());
    }
    if (type.getNodeValue().equalsIgnoreCase("double")) {
      return Double.valueOf(node.getAttributes().getNamedItem("value").getNodeValue());
    }
    if (type.getNodeValue().equalsIgnoreCase("array")) {
      String spiltString = ",";
      Node spilt = node.getAttributes().getNamedItem("spilt");
      if (spilt != null) spiltString = spilt.getNodeValue();
      return node.getAttributes().getNamedItem("value").getNodeValue().split(spiltString);
    }
    return node.getAttributes().getNamedItem("value").getNodeValue();
  }

  private NodeList getNodeList(String dataFile) {
    DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder dombuilder = domfac.newDocumentBuilder();
      InputStream is = new FileInputStream(this.defaultPath + dataFile);
      Document doc = dombuilder.parse(is);
      Element cases = doc.getDocumentElement();
      return cases.getChildNodes();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}