package project1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebParam.Mode;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class dom4j {
	static String compMode = ""; //静态全局变量，对应按不同公司分类的方式
	/**
	 * 
	 * @param node 原始文件节点，Element类型
	 * @param compNode 输出文件节点
	 */
	public static void separate(Element node,Element compNode){ //递归遍历函数
		//System.out.println(node.getName());
			List<Attribute> attrList = node.attributes(); //获取节点所有的属性元素
			for (Attribute attr : attrList){
				//System.out.println(attr.getValue());
				if (node.getName().equals("purchaseOrder")){ //要实现的功能是选出属性名为IBM或ABC的purchaseOrder节点
					if (!attr.getValue().equals(compMode)) return; 
				} else {
					compNode.addElement(attr.getName().toString()); //addElement为添加节点
					compNode.element(attr.getName().toString()).setText(attr.getValue().toString());//设置子节点的值为当前属性值
				}
			}
		
		if (!(node.getTextTrim().equals(""))) {  
            compNode.setText(node.getText());
        }
		
		Iterator<Element> it = node.elementIterator();//迭代器循环获取所有的子节点，并递归调用
	    while (it.hasNext()) {  
	    	Element e = it.next();  
	    	if (node.getName().equals("purchaseOrders")) separate(e,compNode); 
	    	if (!node.getName().equals("purchaseOrders")) {
	    		Element nextCompNode = compNode.addElement(e.getName().toString());
	    		separate(e,nextCompNode); 
	    	}
	    }
	}

	public static void writer(Document document,String filename) throws Exception{ //输出 xml文件
		OutputFormat format = OutputFormat.createPrettyPrint();  
		format.setEncoding("UTF-8");  //设置编码方式
		format.setIndent(true);
		format.setIndent("   ");//设置缩进
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(new File(filename)), "UTF-8"), format);  
		writer.write(document);  //写入文件
		writer.flush();  
		writer.close();  
	}
	
	public static void main(String[] args) throws Exception{
		SAXReader reader = new SAXReader(); //使用SAXReader  读入文件结构
		Document  document = reader.read(new File("/home/xbrother/workspace/xmlWebservice-Project1/asset/ipo.xml")); //创建Document对象
		Element node = document.getRootElement(); //获取根节点node
		
		Document docABC = DocumentHelper.createDocument(); 
		Document docIBM = DocumentHelper.createDocument();
		
		Element ABCElement = docABC.addElement("purchaseOrders");
		ABCElement  = ABCElement.addElement("ABC_COMP");
		Element IBMElement = docIBM.addElement("purchaseOrders");
		IBMElement  = IBMElement.addElement("IBM_COMP");
		//System.out.println(ABCElement.getName());
		
		compMode = "ABC";
		separate(node,ABCElement);
		compMode = "IBM";
		separate(node,IBMElement);
		
		writer(docABC,"/home/xbrother/workspace/xmlWebservice-Project1/asset/ABC_COMP_dom4j.xml");
		writer(docIBM,"/home/xbrother/workspace/xmlWebservice-Project1/asset/IBM_COMP_dom4j.xml");
		
	}
		
}
