package com.zlycare;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.unionpay.acp.sdk.HttpClient;
import com.unionpay.acp.sdk.SDKConstants;
import com.unionpay.acp.sdk.SDKUtil;
import com.unionpay.acp.sdk.SecureUtil;

/**
 * ���ƣ� ��������<br>
 * ���ܣ� �ṩ��������<br>
 * �汾�� 5.0<br>
 * ���ڣ� 2014-07<br>
 * ���ߣ� �й�����ACP�Ŷ�<br>
 * ��Ȩ�� �й�����<br>
 * ˵�������´���ֻ��Ϊ�˷����̻����Զ��ṩ���������룬�̻����Ը����Լ���Ҫ�����ռ����ĵ���д���ô�������ο���<br>
 */
public class UnionBase {

	public static String encoding = "UTF-8";

	/**
	 * 5.0.0
	 */
	public static String version = "5.0.0";

	
	

	/**
	 * http://localhost:8080/ACPTest/acp_front_url.do
	 */
	//��̨�����Ӧ��д������ FrontRcvResponse.java
	public static String frontUrl = "http://localhost:8080/ACPTest/acp_front_url.do";

	public UnionBase() {
		super();
	}

	/**
	 * http://localhost:8080/ACPTest/acp_back_url.do
	 */
//��̨�����Ӧ��д������ BackRcvResponse.java
	public static String backUrl = "http://localhost:8080/ACPTest/acp_back_url.do";// �����ͷ�������ѡ��д����[O]--��̨֪ͨ��ַ

	/**
	 * ����HTTP POST���ױ��ķ���ʾ��
	 * 
	 * @param action
	 *            ���ύ��ַ
	 * @param hiddens
	 *            ��MAP��ʽ�洢�ı���ֵ
	 * @return ����õ�HTTP POST���ױ�
	 */
	public static String createHtml(String action, Map<String, String> hiddens) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + action
				+ "\" method=\"post\">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Entry<String, String>> set = hiddens.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}

	/**
	 * java main���� �����ύ ���� �����ݽ���ǩ��
	 * 
	 * @param contentData
	 * @return��ǩ�����map����
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> signData(Map<String, ?> contentData) {
		Entry<String, String> obj = null;
		Map<String, String> submitFromData = new HashMap<String, String>();
		for (Iterator<?> it = contentData.entrySet().iterator(); it.hasNext();) {
			obj = (Entry<String, String>) it.next();
			String value = obj.getValue();
			if (StringUtils.isNotBlank(value)) {
				// ��valueֵ����ȥ��ǰ��մ���
				submitFromData.put(obj.getKey(), value.trim());
				System.out
						.println(obj.getKey() + "-->" + String.valueOf(value));
			}
		}
		/**
		 * ǩ��
		 */
		SDKUtil.sign(submitFromData, encoding);

		return submitFromData;
	}


	/**
	 * java main���� �����ύ �ύ����̨
	 * 
	 * @param contentData
	 * @return ���ر��� map
	 */
	public static Map<String, String> submitUrl(
			Map<String, String> submitFromData,String requestUrl) {
		String resultString = "";
		System.out.println("requestUrl====" + requestUrl);
		System.out.println("submitFromData====" + submitFromData.toString());
		/**
		 * ����
		 */
		HttpClient hc = new HttpClient(requestUrl, 30000, 30000);
		try {
			int status = hc.send(submitFromData, encoding);
			if (200 == status) {
				resultString = hc.getResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, String> resData = new HashMap<String, String>();
		/**
		 * ��֤ǩ��
		 */
		if (null != resultString && !"".equals(resultString)) {
			// �����ؽ��ת��Ϊmap
			resData = SDKUtil.convertResultStringToMap(resultString);
			if (SDKUtil.validate(resData, encoding)) {
				System.out.println("��֤ǩ���ɹ�");
			} else {
				System.out.println("��֤ǩ��ʧ��");
			}
			// ��ӡ���ر���
			System.out.println("��ӡ���ر��ģ�" + resultString);
		}
		return resData;
	}

	/**
	 * ���������ļ�
	 */
	public static void deCodeFileContent(Map<String, String> resData) {
		// ���������ļ�
		String fileContent = resData.get(SDKConstants.param_fileContent);
		if (null != fileContent && !"".equals(fileContent)) {
			try {
				byte[] fileArray = SecureUtil.inflater(SecureUtil
						.base64Decode(fileContent.getBytes(encoding)));
				String root = "D:\\";
				String filePath = null;
				if (SDKUtil.isEmpty(resData.get("fileName"))) {
					filePath = root + File.separator + resData.get("merId")
							+ "_" + resData.get("batchNo") + "_"
							+ resData.get("txnTime") + ".txt";
				} else {
					filePath = root + File.separator + resData.get("fileName");
				}
				File file = new File(filePath);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file);
				out.write(fileArray, 0, fileArray.length);
				out.flush();
				out.close();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * java main���� �����ύ�� ������װ�����ύ ����ǩ��
	 * 
	 * @param contentData
	 * @return ���ر��� map
	 */
	public static Map<String, String> submitDate(Map<String, ?> contentData,String requestUrl) {

		Map<String, String> submitFromData = (Map<String, String>) signData(contentData);

		return submitUrl(submitFromData,requestUrl);
	}
	
	/**
	 * �ֿ�����Ϣ�����
	 * 
	 * @param encoding  ���뷽ʽ
	 * @return base64��ĳֿ�����Ϣ���ֶ�
	 */
	public static String getCustomer(String encoding) {
		StringBuffer sf = new StringBuffer("{");
		// ֤������
		String certifTp = "01";
		// ֤������
		String certifId = "1301212386859081945";
		// ����
		String customerNm = "����";
		// �ֻ���
		String phoneNo = "18613958987";
		// ������֤��
		String smsCode = "123311";
		// �ֿ�������
		String pin = "123213";
		// cvn2
		String cvn2 = "400";
		// ��Ч��
		String expired = "1212";
		sf.append("certifTp=" + certifTp + SDKConstants.AMPERSAND);
		sf.append("certifId=" + certifId + SDKConstants.AMPERSAND);
		sf.append("customerNm=" + customerNm + SDKConstants.AMPERSAND);
		sf.append("phoneNo=" + phoneNo + SDKConstants.AMPERSAND);
		sf.append("smsCode=" + smsCode + SDKConstants.AMPERSAND);
		// �������
		sf.append("pin=" + SDKUtil.encryptPin("622188123456789", pin, encoding)
				+ SDKConstants.AMPERSAND);
		// ���벻����
		// sf.append("pin="+pin + SDKConstants.AMPERSAND);
		// cvn2����
		// sf.append(SDKUtil.encrptCvn2(cvn2, encoding) +
		// SDKConstants.AMPERSAND);
		// cvn2������
		sf.append("cvn2=" + cvn2 + SDKConstants.AMPERSAND);
		// ��Ч�ڼ���
		// sf.append(SDKUtil.encrptAvailable(expired, encoding));
		// ��Ч�ڲ�����
		sf.append("expired=" + expired);
		sf.append("}");
		String customerInfo = sf.toString();
		try {
			return new String(SecureUtil.base64Encode(sf.toString().getBytes(
					encoding)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return customerInfo;
	}
	

}