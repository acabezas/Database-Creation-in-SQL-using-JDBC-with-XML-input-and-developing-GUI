package CS430;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.ResultSet;

public class Parser {

    private String XMLFilePathName = null;
    private Connection con;
	
	public Parser(String s, Connection con) {
		this.XMLFilePathName = s;
		this.con = con;
	}
	
	public void readXML() {
		
		try {
			File file = new File(XMLFilePathName);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("section");

			StringBuffer ClassId = new StringBuffer();
			StringBuffer SectionNo = new StringBuffer();
			StringBuffer Semester = new StringBuffer();
			StringBuffer Time = new StringBuffer();
			StringBuffer ClassRoom = new StringBuffer();
			StringBuffer EmpNo = new StringBuffer();
			
			Statement stmt = con.createStatement();
			
			for (int s = 0; s < nodeLst.getLength(); s++) {

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element sectionNode = (Element) fstNode;
					
					NodeList classidElementList = sectionNode.getElementsByTagName("ClassId");
					Element classidElmnt = (Element) classidElementList.item(0);
					NodeList classidNodeList = classidElmnt.getChildNodes();
					ClassId.append(((Node) classidNodeList.item(0)).getNodeValue().trim());
					System.out.println("ClassId : "  + ClassId.toString());

					NodeList secnoElementList = sectionNode.getElementsByTagName("SecNo");
					Element secnoElmnt = (Element) secnoElementList.item(0);
					NodeList secno = secnoElmnt.getChildNodes();
					SectionNo.append(((Node) secno.item(0)).getNodeValue().trim());
					System.out.println("Section number : "  + SectionNo.toString());

					NodeList semesterElementList = sectionNode.getElementsByTagName("Semester");
					Element semElmnt = (Element) semesterElementList.item(0);
					NodeList sem = semElmnt.getChildNodes();
					Semester.append(((Node) sem.item(0)).getNodeValue().trim());
					System.out.println("Semester : "  + Semester.toString());
					
					NodeList timeElementList = sectionNode.getElementsByTagName("Time");
					Element timeElmnt = (Element) timeElementList.item(0);
					NodeList time = timeElmnt.getChildNodes();
					Time.append(((Node) time.item(0)).getNodeValue().trim());
					System.out.println("Time : "  + Time.toString());
					
					NodeList ClassRoomElementList = sectionNode.getElementsByTagName("ClassRoom");
					Element ClassRoomElmnt = (Element) ClassRoomElementList.item(0);
					NodeList classRoom = ClassRoomElmnt.getChildNodes();
					ClassRoom.append(((Node) classRoom.item(0)).getNodeValue().trim());
					System.out.println("ClassRoom : "  + ClassRoom.toString());

					NodeList empnoElementList = sectionNode.getElementsByTagName("Emp_no");
					Element empElmnt = (Element) empnoElementList.item(0);
					NodeList emp = empElmnt.getChildNodes();
					EmpNo.append(((Node) emp.item(0)).getNodeValue().trim());
					System.out.println("Professor : "  + EmpNo.toString());

					try{
						ResultSet sectionrs = stmt.executeQuery("select * from SECTIONs where CLASSID = '"
														+ClassId.toString()+"' and SECTIONNUMBER = '"
														+SectionNo.toString()+"' and SEMESTER = '"
														+Semester.toString()+"' and CLASSROOM ='"
														+ClassRoom.toString()+"';");
					    if(!sectionrs.first()) {
					    	
					    	stmt.executeUpdate("insert into SECTIONs VALUES ('"
					    							+ClassId.toString()+"','Section "
					    							+SectionNo.toString()+"','"
					    							+Semester.toString()+"','"
					    							+ClassRoom.toString()+"','"
					    							+Time.toString()+"');");
					    	
					    	System.out.println("Parsing the data in SECTION table, CLASSID: " + ClassId.toString() 
					    						+ ", SECTION: " + SectionNo.toString() + ", SEMESTER: " +Semester.toString()
					    						+ ", CLASSROOM: " + ClassRoom.toString() + ", TIME: " + Time.toString());
					    	
				   	
					    	// Check if CLASSID, EMP_ID, SECTION and SEMESTER is present in TEACHES table
					    	ResultSet Teach = stmt.executeQuery("select * from TEACHES where CLASSID = '"
									+ClassId.toString()+"' and SECTIONNUMBER = '"
									+SectionNo.toString()+"' and SEMESTER = '"
									+Semester.toString()+"' and EMP_NO ="
									+Integer.parseInt(EmpNo.toString())+";");
					    	
					    																// If not, Insert into TEACHES 
					    	if(!Teach.first()) {
					    		stmt.executeUpdate("insert into TEACHES VALUES ('"+Integer.parseInt(EmpNo.toString())+"','"
		    							+ClassId.toString()+"','Section "
		    							+SectionNo.toString()+"','"
		    							+Semester.toString()+"'"
		    							+");");
					    		
					    		System.out.println("Parsing the data in TEACHES table, CLASSID: " + ClassId.toString() 
	    											+ ", SECTION: " + SectionNo.toString() + ", SEMESTER: " +Semester.toString()
	    											+ ", EMP_NO: " + EmpNo.toString());
					    	}
					    	
		
					    	// Check if CLASSID, EMP_ID is present in QUALIFIED table
					    	ResultSet Qual = stmt.executeQuery("select * from QUALIFIED where CLASSID = '"
									+ClassId.toString()+"'and EMP_NO ="
									+Integer.parseInt(EmpNo.toString())+";");
					    	
					    																//If not, Insert into QUALIFIED
					    	if(!Qual.first()) {					    		
					    		stmt.executeUpdate("insert into QUALIFIED values ("
					    						+Integer.parseInt(EmpNo.toString())+",'"
					    						+ClassId.toString()+"');");
					    		
					    		System.out.println("Parsing the data in QUALIFIED table, EMP_NO: " + EmpNo.toString() + ", CLASSID : " + ClassId.toString());
					    		
					    	}
					    }

					 }catch(Exception e){
					     System.out.print(e);
					     e.printStackTrace();
					 }
					

					ClassId.setLength(0);
					SectionNo.setLength(0);
					Semester.setLength(0);
					Time.setLength(0);
					ClassRoom.setLength(0);
					EmpNo.setLength(0);
					System.out.println();
				}
				
			}
			
			String finalSql = "select C.*, S.SECTIONNUMBER, S.TIME, "
					+ "CONCAT(P.FIRSTNAME, ' ', P.LASTNAME) as PROF_NAME, S.CLASSROOM from CLASS C, SECTIONs S, PROFESSOR P, TEACHES T "
					+ "where C.CLASSID = S.CLASSID and T.CLASSID = C.CLASSID and T.EMP_NO = P.EMP_NO and S.SEMESTER = 'SP16' "
					+ "order by C.CLASSID, S.SECTIONNUMBER;";
			
			ResultSet rs = stmt.executeQuery(finalSql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			
			while(rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
			        if (i > 1) System.out.print(",  ");
			        String columnValue = rs.getString(i);
			        System.out.print(rsmd.getColumnName(i) + " : " + columnValue + " " );
			    }
			    System.out.println("");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	} 
}
