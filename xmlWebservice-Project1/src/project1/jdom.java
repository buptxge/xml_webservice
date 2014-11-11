package project1;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class jdom {
	static String compMode = ""; //静态全局变量，对应按不同公司分类的方式
	static Document docABC =  new Document();
	static Document docIBM = new Document();
	public static void outputer(Document document,String filename) throws Exception{ //输出 xml文件
		XMLOutputter out = new XMLOutputter();
		Format format = Format.getPrettyFormat(); 
		format.setIndent("   ") ;  
        out.setFormat(format);  
        out.output(document, new FileOutputStream(filename));  
	}
	
	public static void separate(Element node,Element compNode) throws Exception{ //递归遍历函数

		List<Attribute> attrList = node.getAttributes(); //获取节点所有的属性元素
		int attrListSize = attrList.size();
		
		for (int j=0; j<attrListSize; j++) {
			Attribute attr = (Attribute) attrList.get(j);
			if (node.getName().equals("purchaseOrder")){ //要实现的功能是选出属性名为IBM或ABC的purchaseOrder节点
				if (!attr.getValue().equals(compMode)) return; 
			} else {
				Element newAttrElement = new Element(attr.getName().toString());
				newAttrElement.setText(attr.getValue().toString());
				compNode.addContent(newAttrElement); //addContent为添加节点,这里是将属性转化为节点
				//System.out.println("当前公司添加的子节点为："+compNode.getChild(attr.getName()).getName());
			}
		}
			
		if (!(node.getTextTrim().equals(""))) {  
            compNode.setText(node.getText());
            //System.out.println("公司对象添加了"+node.getText());
        }
		
		List <Element> nodeChildren = node.getChildren();  //获取子节点的list集合
		int listsize = nodeChildren.size();
		
	    for (int i=0; i<listsize; i++) {  
	    	Element child = (Element) nodeChildren.get(i).clone();
	    	if (node.getName().equals("purchaseOrders")) separate(child,compNode); else {
		    	//System.out.println("当前子节点为"+child.getName());
	    		compNode.addContent(new Element(child.getName().toString()));
	    		int lastChild = compNode.getChildren().size();
	    		Element nextCompNode = (Element) compNode.getChildren().get(lastChild-1);
	    		separate(child,nextCompNode); 
	    	}
	    }
	}
	
	public static void main(String[] args) throws Exception {
		SAXBuilder builder = new SAXBuilder(false);
		Document doc  = builder.build("/home/xbrother/workspace/xmlWebservice-Project1/asset/ipo.xml");
		Element node = doc.getRootElement();
		
		Element ABCRoot = new Element("purchaseOrders");
		docABC.addContent(ABCRoot);
		ABCRoot.addContent(new Element("ABC_COMP"));//初始化设置ABC_COMP的开始元素
		Element ABCNode = ABCRoot.getChild("ABC_COMP");
		ABCNode.addContent(new Element("purchaseOrder"));
		ABCNode = ABCNode.getChild("purchaseOrder");
		
		Element IBMRoot = new Element("purchaseOrders");
		docIBM.addContent(IBMRoot);
		IBMRoot.addContent(new Element("IBM_COMP"));//初始化设置IBM_COMP的开始元素
		Element IBMNode = IBMRoot.getChild("IBM_COMP");
		IBMNode.addContent(new Element("purchaseOrder"));
		IBMNode = IBMNode.getChild("purchaseOrder");
		
		//Element newNode = (Element) node.getChild("purchaseOrder").clone();
		//IBMNode.addContent(newNode);
		compMode = "ABC";
		separate(node, ABCNode);
		outputer(docABC,"/home/xbrother/workspace/xmlWebservice-Project1/asset/ABC_COMP_jdom.xml");
		
		compMode = "IBM";
		separate(node, IBMNode);
		outputer(docIBM,"/home/xbrother/workspace/xmlWebservice-Project1/asset/IBM_COMP_jdom.xml");
		
		
	}
}
