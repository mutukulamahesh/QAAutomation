package com.oracle.hgbu.opera.qaauto.ws.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EmailUtil {
	static List<String> filesListInDir = new ArrayList<String>();
	
	public static void SendMail(String aStrTORecipients, String aStrCCRecipients, String aStrSubject, String aStrMessage, String aStrattachmentPath,
			String aStrFrom, String SMTP_HOST_NAME, String SMTP_PORT) throws Exception {
		boolean debug = false;
		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", SMTP_HOST_NAME);
			props.put("mail.smtp.port", SMTP_PORT);
			Session lObjSession = Session.getDefaultInstance(props, null);
			Message message = new MimeMessage(lObjSession);
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();
			InternetAddress lObjInternetAddressFrom = new InternetAddress(aStrFrom);
			InternetAddress[] lObjInternetAddressTo = null;
			
			message.setFrom(lObjInternetAddressFrom);
			if (aStrTORecipients.contains(";")) {
				String[] lArrStrRecipients = aStrTORecipients.split(";");
				if (lArrStrRecipients == null) {
					System.out.println(
							"Please provide recipient address list in this format.  aaa@123.com;bbb@123.com... etc");
					return;
				}
				int lIntToCount = lArrStrRecipients.length;
				lObjInternetAddressTo = new InternetAddress[lIntToCount];
				for (int lIntIndex = 0; lIntIndex < lIntToCount; lIntIndex++) {
					String lStrToMailIDTemp = lArrStrRecipients[lIntIndex].trim();
					if (lStrToMailIDTemp.trim() == "")
						continue;
					if (!ValidateEmail(lStrToMailIDTemp)) {
						System.out.println("Invalid recipient email ID(s).");
						return;
					}
					lObjInternetAddressTo[lIntIndex] = new InternetAddress(lStrToMailIDTemp);
				}

			} else {
				if (!ValidateEmail(aStrTORecipients)) {
					System.out.println("Invalid recipient email ID(s).");
					return;
				}
				lObjInternetAddressTo = new InternetAddress[1];
				lObjInternetAddressTo[0] = new InternetAddress(aStrTORecipients.trim());
			}
			
			message.addRecipients(Message.RecipientType.TO, lObjInternetAddressTo);
			if (aStrCCRecipients != null) {
				if (aStrCCRecipients.trim() != "") {
					InternetAddress[] lObjInternetAddressCC = null;
					if (aStrCCRecipients.contains(";")) {
						String[] lArrStrRecipients = aStrCCRecipients.split(";");
						if (lArrStrRecipients == null) {
							System.out.println(
									"Please provide recipient address list in this format.  aaa@123.com;bbb@123.com... etc");
							return;
						}
						int lIntCCCount = lArrStrRecipients.length;
						lObjInternetAddressCC = new InternetAddress[lIntCCCount];
						for (int lIntIndex = 0; lIntIndex < lIntCCCount; lIntIndex++) {
							String lStrCCMailIDTemp = lArrStrRecipients[lIntIndex].trim();
							if (lStrCCMailIDTemp.trim() == "")
								continue;
							if (!ValidateEmail(lStrCCMailIDTemp)) {
								System.out.println("Invalid recipient email ID(s).");
								return;
							}
							lObjInternetAddressCC[lIntIndex] = new InternetAddress(lStrCCMailIDTemp);
						}
					} else {
						if (!ValidateEmail(aStrCCRecipients)) {
							System.out.println("Invalid CC recipient email ID(s).");
							return;
						}
						lObjInternetAddressCC = new InternetAddress[1];
						lObjInternetAddressCC[0] = new InternetAddress(aStrCCRecipients.trim());
					}
					message.addRecipients(Message.RecipientType.CC, lObjInternetAddressCC);
				}
			}

			DataSource source = new FileDataSource(aStrattachmentPath);			
			// Setting the Subject and Content Type			
			message.setSubject(aStrSubject);
			//message.setContent(aStrMessage, "text/html");
			//messageBodyPart.setText(aStrMessage);
			messageBodyPart.setContent(aStrMessage, "text/html; charset=utf-8");
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(aStrattachmentPath);
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			message.setSentDate(new Date());
			
			try {
				Transport.send(message);
				System.out.println("Mail sent.");
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage().toString());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static boolean ValidateEmail(String sEmail) {
		try {
			String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			Pattern pattern;
			Matcher matcher;
			pattern = Pattern.compile(EMAIL_PATTERN);
			matcher = pattern.matcher(sEmail);
			return matcher.matches();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

   private static void zipDirectory(File dir, String zipDirName) {
	        try {
	            populateFilesList(dir);
	            //now zip files one by one
	            //create ZipOutputStream to write to the zip file
	            FileOutputStream fos = new FileOutputStream(zipDirName);
	            ZipOutputStream zos = new ZipOutputStream(fos);
	            for(String filePath : filesListInDir){
	                System.out.println("Zipping "+filePath);
	                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
	                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
	                zos.putNextEntry(ze);
	                //read the file and write to ZipOutputStream
	                FileInputStream fis = new FileInputStream(filePath);
	                byte[] buffer = new byte[1024];
	                int len;
	                while ((len = fis.read(buffer)) > 0) {
	                    zos.write(buffer, 0, len);
	                }
	                zos.closeEntry();
	                fis.close();
	            }
	            zos.close();
	            fos.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	  
    private static void populateFilesList(File dir) throws IOException {
	        File[] files = dir.listFiles();
	        for(File file : files){
	            if(file.isFile()) {
	            	if(!file.getName().equalsIgnoreCase("TimeMachine.java") && !file.getName().equalsIgnoreCase("tmpMessageBody.html") && !file.getName().equalsIgnoreCase("log.out"))
	            	 filesListInDir.add(file.getAbsolutePath());
	            }
	            else {
	            	populateFilesList(file);
	            }
	        }
	}
    
    public static String zip(String folderPath) {
    	File dir = new File(folderPath);
        String zipDirName = folderPath+".zip";
        zipDirectory(dir, zipDirName);
        return zipDirName;
    }
    
    public static void main(String args[]) throws Exception {
    	String resultDir = "D:\\automation\\int\\resources\\results\\Run_20190122_1601869";
      	String zipDir = EmailUtil.zip(resultDir);
      	File file = new File(resultDir+"\\"+"tmpMessageBody.html");
      	String bodyStr = FileUtils.readFileToString(file);
    	EmailUtil.SendMail("santhoshi.basana@oracle.com", "uday.vangala@oracle.com", "Opera Integration Automation Suite for Interfaces BAT on 5.6 LP",
    			bodyStr, zipDir, "uday.vangala@oracle.com", "internal-mail-router.oracle.com", "25");
    }
    
    
    
}