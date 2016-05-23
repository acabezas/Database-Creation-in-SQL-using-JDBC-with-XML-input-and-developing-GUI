//package CS430;

import java.awt.GridLayout;
import java.sql.*;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Scanner;

import javax.swing.*;

public class GUI {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String args[]) throws Exception{
		
		Statement stmt;
	    int rs1=0;
	    ResultSet rs2 = null;		
	    Connection con = null;
	    
	    // Register the JDBC driver for MySQL.
	    Class.forName("com.mysql.jdbc.Driver");

	    // Define URL of database server for
	    // database named 'user' on the faure.
	    String url ="jdbc:mysql://faure/aksdarak";
	    
	    // Get a connection to the database for a
	    // user named 'user' with the password
	    // 123456789.
	    con = DriverManager.getConnection(url,"aksdarak", "830701950");
	    
	    // Display URL and connection information
	    System.out.println("URL: " + url);
	    System.out.println("Connection: " + con);

	    // Get a Statement object
		stmt = con.createStatement();
		
		//First asks the user for Student_ID
		while(true){
		int StudentId = Integer.parseInt(JOptionPane.showInputDialog ("Enter a StudentId:"));
		
		try{
            rs2 = stmt.executeQuery("SELECT STU_ID FROM STUDENT WHERE STU_ID="+StudentId);
            
          }catch(Exception e){
            System.out.print(e);
            
          }
		
		int registration=0;
		
		if(rs2.next()){																//Checks if the user already exists
			JOptionPane.showMessageDialog(null, "The entered StudentID Exists");
		}
		else{
			JOptionPane.showMessageDialog(null,"Student does not exist in database, registration required.");
			registration=1;
		}
		
		JPanel myPanel = new JPanel();
	      
			if(registration==1){
				int regStudentId = StudentId;								
				
				myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
				
				myPanel.add(new JLabel("FIRST_NAME:"));						//Registers the student by taking all the required attributes
				JTextField Firstname = new JTextField(10);
				myPanel.add(Firstname);
				
				myPanel.add(new JLabel("LAST_NAME:"));
				JTextField Lastname = new JTextField(10);
				myPanel.add(Lastname);
				
				myPanel.add(new JLabel("BIRTH_DATE:"));
				JTextField Birthdate = new JTextField(10);
				myPanel.add(Birthdate);
				
				myPanel.add(new JLabel("MAJOR:"));
				JTextField Major = new JTextField(2);
				myPanel.add(Major);
				
				JOptionPane.showMessageDialog(null, myPanel);
				
				try{
		            rs1 = stmt.executeUpdate("insert into STUDENT values("+regStudentId+", '"+Firstname.getText()+"', '"+Lastname.getText()+"', '"+Birthdate.getText()+"', '"+Major.getText()+"');");
		            
		          }catch(Exception e){
		            System.out.print(e);
		          }
				registration=0;
			}
			
			String Semester = JOptionPane.showInputDialog ("Enter semester to enroll. FA15 or SP16?");					//Semester to enroll
			if(!Semester.equals("FA15") && !Semester.equals("SP16")){
				JOptionPane.showMessageDialog(null, "Invalid semester. Please try again");
				System.exit(0);
			}
			
			
			ArrayList<String> enrolled=new ArrayList<String>();
			try{
	            rs2 = stmt.executeQuery("select CLASSID from ENROLLED where STU_ID="+StudentId+";");					//Get all the courses student has enrolled
	            while(rs2.next()){
	            	enrolled.add(rs2.getString(1));
	            	}
	          }catch(Exception e){
	            System.out.print(e);
	          }
			
			
			ArrayList<String> Classid=new ArrayList<String>();				//Array Declaration for ClassIDs
			ArrayList<String> prereq=new ArrayList<String>();				//Array Declaration for Pre-requisites
			
			try{
	            rs2 = stmt.executeQuery("select CLASSID, group_concat(PreReq_ClassID) as Prereq from CLASS_PREREQ group by CLASSID;");
	            while(rs2.next()){
	            	Classid.add(rs2.getString(1));
	            	prereq.add(rs2.getString(2));
	            }
	          }catch(Exception e){
	            System.out.print(e);
	          }
			
			String[] esize= new String[enrolled.size()];
			String[] classsize= new String[Classid.size()];
			String[] reqsize= new String[prereq.size()];
			
			for(int i=0;i<enrolled.size();i++){
				esize[i]=enrolled.get(i);
				//System.out.println("enrolled classes : "+ esize[i]);
			}
			
			for(int i=0;i<Classid.size();i++){
				classsize[i]=Classid.get(i);
				reqsize[i]=prereq.get(i);
				//System.out.println("Classes : "+ classsize[i]);
				
			}
			
			String[] qualified = new String[20];
			int qual=0;
			
			for(int i=0;i<classsize.length;i++){							//Find the courses student can enroll for
				String[] prereqsep = reqsize[i].split(",");
				for(int i1=0;i1<prereqsep.length;i1++){
					prereqsep[i1]=prereqsep[i1].trim();
					//System.out.println("Enroll : "+ prereqsep[i]);
				}
				boolean b = Arrays.asList(esize).containsAll(Arrays.asList(prereqsep));
				if(b==true){
					qualified[qual]=classsize[i];
					System.out.println(qualified[qual]);
					qual++;
				}
			}
			
																			//Only adding those courses that do not have any prerequisites
			try{																	
	            rs2 = stmt.executeQuery("SELECT PreReq_ClassID from CLASS_PREREQ WHERE PreReq_ClassID NOT IN (SELECT CLASSID from CLASS_PREREQ);");
	            while(rs2.next()){
	            	qualified[qual]=rs2.getString(1);											
	            	qual++;
	            }
	          }catch(Exception e){
	            System.out.print(e);
	          }

			ArrayList<String> Options = new ArrayList<String>();
			int limit=6;
			String[] Chosen = new String[6];
			
																			 //Courses that the student can take				
			try{
				JPanel myPanel2 = new JPanel();
				myPanel2.setLayout(new GridLayout(0,1));
				myPanel2.add(new JLabel("Courses that can be enrolled are:"));
				JComboBox[] model = new JComboBox[6];
				Options.add("Blank");
				for(int i=0;i<qual;i++){
		            rs2 = stmt.executeQuery("select S.CLASSID, S.SECTIONNUMBER, C.No_of_Credits from SECTIONs S, CLASS C where S.CLASSID='"+qualified[i]+"' and S.CLASSID = C.CLASSID and S.SEMESTER='"+Semester+"';");
		            
		            while (rs2.next()) {
		            	myPanel2.add(new JLabel(rs2.getString(1)+"    "+rs2.getString(2)+"    "+rs2.getString(3)));
		            	Options.add(rs2.getString(1)+"  "+rs2.getString(2)+"  "+rs2.getString(3));
		            }
				}
	            String[] options1= new String[Options.size()];
	            for(int i=0;i<Options.size();i++){
	            	options1[i]=Options.get(i);
	            }
	            
	            for(int i=0;i<6;i++){
	            	model[i]=new JComboBox(options1);
	            }
	            
	            myPanel2.add(new JLabel("Select classes to register:(CLASSID, SECTIONNUMBER, No_of_Credits)"));
				
	            for(int i=0;i<6;i++){
	            	myPanel2.add(model[i]);
	            }

	            JOptionPane.showMessageDialog(null, myPanel2);
				int CreditSum=0;
				int SubNum=6;
				
				for(int i=0;i<6;i++){
					if(!((String) model[i].getSelectedItem()).equals("Blank")){
						Chosen[i] = ((String) model[i].getSelectedItem());
					}
					else
					{
						SubNum--;
					}
					System.out.println(SubNum);
				}
				
				limit = SubNum;
				for(int i=0;i<SubNum;i++){
					String[] ChosenSplit = ((String) model[i].getSelectedItem()).split(" ");
					rs2 = stmt.executeQuery("select No_of_Credits from CLASS where CLASSID='"+ChosenSplit[0]+"';");
					while (rs2.next()) {
						if((CreditSum + Integer.parseInt(rs2.getString("No_of_Credits")))<=18){
							CreditSum = CreditSum + Integer.parseInt(rs2.getString("No_of_Credits"));
						}
						else{
							if(limit>i){
								limit=i;
							}
							JOptionPane.showMessageDialog(null, ChosenSplit[0]+" cannot be registered. Maximum 18 credit hours can be registered");
							}
		            }
				}
				
	          }catch(Exception e){
	            System.out.print(e);
	          }
			
		
			JPanel myPanel3 = new JPanel();
			myPanel3.setLayout(new GridLayout(0,1));
			
			String comb="";
			for(int i = 0; i < limit; i++){
			   comb = comb + Chosen[i]+"\n";
			}
			
			int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to enroll for these courses?\n"+comb, "VERIFICATION SCREEN",
			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.YES_OPTION) {
				for(int i = 0; i < limit; i++){
					String[] spt = Chosen[i].split(" ");
					try{
			            rs1 = stmt.executeUpdate("insert into ENROLLED values("+StudentId+",'"+spt[0]+"',"+spt[2]+",'"+Semester+"',"+null+","+null+");");
			            System.out.println(rs1+" rows affected");
					}catch(Exception e){
			            System.out.print(e);
			          }
				}
			}    
			else{
			    	JOptionPane.showMessageDialog(null, "Student was not enrolled to the courses.");
			   }  
		}
	}
}

